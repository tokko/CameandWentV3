package com.tokko.cameandwentv3.events
import com.tokko.cameandwentv3.model.Setting

/**
 * Created by andre on 21/07/2017.
 */
class EventSettingsChanged(setting: Setting? = null) {
    var setting: Setting? = setting
}