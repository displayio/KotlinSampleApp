package com.example.kotlinsampleapp

enum class AdUnitType {
    INTERSTITIAL("Interstitial", "4654"),
    INFEED("Infeed", "4655"),
    REWARDED_VIDEO("Rewarded video", "3266"),
    INTERSCROLLER("Interscroller", "6430"),
    BANNER("Banner", "6428"),
    MEDIUM_RECTANGLE("Medium rectangle", "6429"),
    OUTSTREAM_VIDEO_SNAP("Outstream video Snap", "6735"),
    OUTSTREAM_VIDEO_NO_SNAP("Outstream video NoSnap", "6955");

    val adUnitTitle: String
    val placementId: String

    constructor(adUnitTitle: String, placementId: String) {
        this.adUnitTitle = adUnitTitle
        this.placementId = placementId
    }
}
