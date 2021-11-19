package com.example.kotlinsampleapp

enum class AdUnitType {
    INTERSTITIAL("Interstitial", "4654"),
    INFEED("Infeed", "4655"),
    INTERSCROLLER("Interscroller", "6430"),
    BANNER("Banner", "6428"),
    MEDIUM_RECTANGLE("Medium rectangle", "6429"),
    HEADLINE_VIDEO_SNAP("Headline video Snap", "6735"),
    HEADLINE_VIDEO_NO_SNAP("Headline video NoSnap", "6955");

    val adUnitTitle: String
    val placementId: String

    constructor(adUnitTitle: String, placementId: String) {
        this.adUnitTitle = adUnitTitle
        this.placementId = placementId
    }
}
