---
<h1>
ISSSUE RESOLVED</H1>
<p> here's the answer if anyone's curious</p>
<p>
  <code>
    .withClusterProperty("sum", literal("+"), Expression.toNumber(get("aqi")))
              &&
PropertyFactory.textField(Expression.toString(
        Expression.ceil(Expression.division(get("sum"),get("point_count"))))
  </code>
</p>
<img src="https://i.imgur.com/V3p0EYg.png">
---

<h1 id="aqi-data-visualization-blocker">AQI Data Visualization Blocker</h1>
<p>(TLDR: Skip to Problem heading below:)</p>
<p>Over the last few days I’ve been working on writing a series of classes and methods to support displaying AQI data in the most robust and appealing way possible. Although I’ve had some pretty solid success in figuring out how to get the data, strategies to maximize the resolution of the data while not making unnecessary queries to the backend and then producing appropriate features collections in geoJson out of that data, getting the data to display as I would like to using MapBox’s dynamic styling has managed to completely stop me in my tracks.</p>
<p>The issue seems to lie in the complicated and esoteric way MapBox handles Expression() within it’s specification for styling.  the two references that are most appropriate I’ve found on this topic are at:</p>
<p><a href="https://docs.mapbox.com/android/maps/overview/expressions/">https://docs.mapbox.com/android/maps/overview/expressions/</a><br>
<a href="https://docs.mapbox.com/mapbox-gl-js/style-spec/#expressions">https://docs.mapbox.com/mapbox-gl-js/style-spec/#expressions</a></p>
<p>I may simply be dense, but despite really combing these resources, stack overflow and google, i have yet to find a clear path to implementing the behavior  I want to and so I’m hoping someone who reads this will have some insight.</p>
<h2 id="background-info">background info:</h2>
<p>We are using Circle Layer Clustering as a template for designing this dynamic data style<br>
<a href="https://docs.mapbox.com/android/maps/examples/circle-layer-clustering/">https://docs.mapbox.com/android/maps/examples/circle-layer-clustering/</a></p>
<p>The GeoJson our Domain Specific Language produces after collecting the AQI data from the backend servers looks like so:</p>
<pre><code>			      = Example of Typical geojson= 
  {"type":"FeatureCollection","features":[{
  "type":"Feature",
  "geometry":{
  "type":"Point",
  "coordinates": [-118.80991,34.40426]},
  "id":"266"
  ,"properties":
  {"name":"Piru - Pacific, Ventura, California",
  "aqi":"32","co":4.5,"dew":null,"b":33.5,
  "no2":10.2,"o3":32.0,"pm10":2.0,"pm25":5.0,
  "r":null,"so2":null,"t":15.8}},
  {"type":"Feature","geometry":{"type":"Point","coordinates":[-118.8705,34.21014]},"id":"325",
  "properties":{"name":"Thousand Oaks-Moorpark Road, Ventura, California",
  "aqi":"31","co":4.5,"dew":null,"b":32.5,"no2":10.2,"o3":31.2,
  "pm10":2.0,"pm25":5.0,"r":null,"so2":null,"t":15.5}}
</code></pre>
<p>All features can be expected to have AQI in an integer range of 0-aprox 600<br>
All features have a unique id, a name that is not null and a collection of other finer measures of air pollution, some of which may by null depending on what station is providing the data (not all stations provide all data/not all data is available)</p>
<h2 id="problem">Problem</h2>
<p>The actual problem to fix is within the following code, namely: when we are defining the clusters and their properties. In these clusters of features we essentially have an array of Features, we want to sum certain properties of that array of features and then average that sum by the number of features that had a value that was not null of that property…<br>
e.g.<br>
if we had a cluster of 3 features, we would want to be able to say something like<br>
val x = feature[0].aqi.value + feature[1].aqi.value + feature[2].aqi.value<br>
val divisor = for (i in feature.indicies) { if (feature[i].aqi!=null) divisor++}</p>
<pre><code>   fun createStyleFromGeoJson(geoJson: String) {  
  targetMap.setStyle(Style.LIGHT) { style -&gt;  
  try {  
  
  style.addSource(   
 GeoJsonSource("aqiID",  
  // Point to GeoJSON data.   
 com.mapbox.geojson.FeatureCollection.fromJson(geoJson),  
  GeoJsonOptions()  
  .withCluster(true)  
  .withClusterMaxZoom(14)  
  .withClusterRadius(50)  
 ) )  
  //Creating a marker layer for single data points  
 // this mostly works as i want, i.e. it displays the AQI of each feature using Expression.get("aqi")  val unclustered = SymbolLayer("unclustered-points", "aqiID")  
  unclustered.setProperties(  
  PropertyFactory.textField(Expression.get("aqi")),  
  PropertyFactory.textSize(40f),  
  PropertyFactory.iconImage("cross-icon-id"),  
  PropertyFactory.iconSize(  
  Expression.division(  
  Expression.get("aqi"), Expression.literal(1.0f)  
 ) ),  
  PropertyFactory.iconColor(  
  Expression.interpolate(Expression.exponential(1), Expression.get("aqi"),  
  Expression.stop(30.0, Expression.rgb(0, 40, 0)),  
  Expression.stop(60.5, Expression.rgb(0, 80, 0))  
 ) ) )  unclustered.setFilter(Expression.has("aqi"))  
  style.addLayer(unclustered)  
               
 // Use the  GeoJSON source to create three layers: One layer for each cluster category.  
 // Each point range gets a different fill color.             //this seems fine as the point ranges as set do adjust the color of the collections  
  val layers = arrayOf(intArrayOf(30,  
  ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorOne)),  
  intArrayOf(20, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorTwo)),  
  intArrayOf(0, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorThree)))  
  for (i in layers.indices) { //Add clusters' circles  
  val circles = CircleLayer("cluster-$i", "aqiID")  
  circles.setProperties(  
  PropertyFactory.circleColor(layers[i][1]),  
  PropertyFactory.circleRadius(22f)  
 )                   
 
 //this is where i'm lost, so i more or less get whats going on here, point_count is a property  
 // of the feature collection and then we what color to set based 
 //on that point count -- but how would we agregate the total value 
 //of one of the propertis of the features and then average that sum
 // by point count?  
 
  // Add a filter to the cluster layer that hides the circles based on "point_count"  
  circles.setFilter(  
  if (i == 0) Expression.all(Expression.has("point_count"),  
  Expression.gte(pointCount, Expression.literal(layers[i][0]))  
 ) else Expression.all(Expression.has("point_count"),  
  Expression.gte(pointCount, Expression.literal(layers[i][0])),  
  Expression.lt(pointCount, Expression.literal(layers[i - 1][0]))  
 ) )  style.addLayer(circles)  
 }  //Add the count labels that same sum i would like to display here where point_count is currently being displayed  
  val count = SymbolLayer("count", "aqiID")  
  count.setProperties(  
  
  PropertyFactory.textField(Expression.toString(Expression.get("point_count"))), //Expression.toString(Expression.get("point_count"))  
  PropertyFactory.textSize(12f),  
  PropertyFactory.textColor(Color.WHITE),  
  PropertyFactory.textIgnorePlacement(true),  
  PropertyFactory.textAllowOverlap(true)  
 )  style.addLayer(count)  
 } catch (uriSyntaxException: URISyntaxException) {  
  Timber.e("Check the URL %s", uriSyntaxException.message)  
 }  }
</code></pre>
<h2 id="possible-solutions">Possible solutions</h2>
<p>Aside from asking for help through this issue and hoping someone just knows how to get this working, other solutions include:<br>
#1: I keep fiddling with it. Eventually I’ll get it working, I truly believe that, however I have to move this to the back burner as it’s taking up too much of my time fruitlessly<br>
#2: using an alternative dynamic style. I’m absolutely supportive of this! Please if you have concrete suggestions of better ways to communicate this data, let me know!<br>
#3: The easy way out: Just make symbols for each aqi station that will display the AQI and onclick pop up more detailed info. This is of course doable but just… not terribly interesting or impressive.</p>
<h2 id="screenshots">screenshots</h2>
<p>the following show the current state of things, you can see that the number of features is being displaying inside the circles right now instead of an average property (aqi) additionally you can see that at the max zoom, aqi succussfully displays<br>
<img src="https://i.imgur.com/Upv6UjW.png?4" alt="highest zoom"><br>
<img src="https://i.imgur.com/bTlMe43.png?1" alt="medium"><br>
<img src="https://i.imgur.com/obe9nPq.png?1" alt="lowest"></p>

