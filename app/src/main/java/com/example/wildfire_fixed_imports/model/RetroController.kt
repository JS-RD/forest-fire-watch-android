package com.example.wildfire_fixed_imports.model


/*
*
* retrofit controlelr
*
* will manage the data calls through retrofit
*
* class that runns periodic method calls to retrofit to get data and then sends it on the the view model
*
* every 60 seconds, call a ssuspended function to retrofitimplementation, when async await completed then send that data
* to the viewmodel and if neccesarry viewmodel.difutil, if viewmodel finds difference, it tells map controler which draws the differences.
*
*
*
* */

class RetroController : Controller{}