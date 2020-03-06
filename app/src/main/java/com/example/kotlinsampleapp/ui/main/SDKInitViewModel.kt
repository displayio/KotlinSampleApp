package com.example.kotlinsampleapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SDKInitViewModel : ViewModel() {

     val loadEnabled = MutableLiveData<Boolean>()
     val showEnabled = MutableLiveData<Boolean>()
}