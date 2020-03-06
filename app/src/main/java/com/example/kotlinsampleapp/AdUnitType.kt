package com.example.kotlinsampleapp

enum class AdUnitType {
    INTERSTITIAL("Interstitial", "4654"),
    INFEED("Infeed", "4655"),
    REWARDED_VIDEO("Rewarded video", "3266"),
    INTERSCROLLER("Interscroller", "6430"),
    BANNER("Banner", "6428"),
    MEDIUM_RECTANGLE("Medium rectangle", "6429");

    val value: String
    val placementId: String

    constructor(name: String, placementId: String) {
        this.value = name
        this.placementId = placementId
    }
}
