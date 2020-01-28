---


---

<h1 id="introduction">Introduction:</h1>
<p>This document is written in hopes of providing meaningful information about concrete technical details and the rational behind abstract choices made in the building of this application. We, the developers, are almost entirely new to creating software and it is extremely likely that some of the choices made were/are incorrect, further it’s likely there are bugs in the codes and perhaps errors in this document. We ask that you offer some amount of charity in your understanding and provide us any feedback you have – if there’s a question you have that is not addressed in this document OR if you have a suggestion, please contact either of us at :</p>
<p>Jack Seymour<br>
<a href="mailto:stilljack@gmail.com">stilljack@gmail.com</a><br>
(Architechture and “backend” design)</p>
<p>Ronnie Dipple<br>
<a href="mailto:ronniedipple@gmail.com">ronniedipple@gmail.com</a><br>
(UI, customer facing and “Frontend” design)</p>
<h1 id="central-dogma">Central Dogma:</h1>
<p>Wildfire Watch is an Application with one job: Visualize data that relates to major fires, especially those which may threaten human life.<br>
Wildfire Watch should be as simple as possible, providing users with as direct a pipeline to as much data as possible without sacrificing customization and freedom to view the data in a way each user finds both productive and attractive.<br>
Wildfire Watch IS the Map. Every other screen is a distraction and should exist only as long as needed and with as little distraction as possible (without losing aesthetic charms)<br>
We can’t try to save lives, Wildfire Watch is first an entertainment and educational tool – we can provide notifications of fires close to you, we can share our best predictions of where fires will spread to, but we can not provide you with meaningful advice about fires, past or present.</p>
<h2 id="overview-of-architecture-wild-fire-watch-android">Overview of architecture: Wild Fire Watch Android</h2>
<p>With our beliefs about what this app should be in mind, we decided an architecture that in the broadest terms would:<br>
1: Be a single activity app, centered around a fragment containing a map, surrounded by minimal user controls and with access to view additional screens strictly in place to allow more in depth user configuration of the map.</p>
<p>2: Be imperative but limit state as much as possible. We use an organizational system that is explicitly MVVM but without religious adherence to MVVM principles where other solutions have sufficient appeal. We are not building a functional program and classes will have state, we are not building a reactive program and methods will often tell, rarely broadcast.</p>
<p>3: The map is the center of the app and where we should be expecting our users to spend most of their time.  We should do as much as possible to expedite any process that takes them away from the  Map. We should not add features that don’t reflect the central data visualization goals of this app, this includes survival documentation, check lists and static content.</p>
<p>4:follows a structure that can be visualized as such<br>
<a href="https://sketchboard.me/TBW0hRd1TSJL#/">https://sketchboard.me/TBW0hRd1TSJL#/</a><br>
<img src="https://i.imgur.com/iY7jyF1.png" alt="check out our sketchboard!"></p>
<p>5: Fragments talk to viewmodels<br>
viewmodels talk to the MasterCoordinator<br>
The MasterCoordinator gets its data from network controllers<br>
and sends that data on to map controller<br>
map controllers write to the map.</p>
<p>6: Controllers and services should not be stateful – they should be means for other classes to read, write  or use data.</p>
<h2 id="smaller-and-more-specific-topics">Smaller and more specific topics:</h2>
<p>The by lazy delegate is used extensively throughout this application to avoid initialization happening out of order and in hopes that services will be created just in time and the ApplicationLevelProvider class is used to store these services and objects so as to not force recreation unnecessarily.</p>
<p>The GeoJson DSL is provided to allow easy construction of combined feature lists from disparate objects on the fly in an easy to read and easy to write Kotlin-friendly manner. While we only have fires and Air quality data now, that may change in the future to include road directions or others sorts of data that will need to be concatenated into GeoJson Feature Collections. See the example present in the ReadMe or take a look at the first example of its use in AQIDrawController.</p>

