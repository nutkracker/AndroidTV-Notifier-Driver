/*
 *  AndroidTV Notifier Driver    v0.2
 *
 *  Copyright 2021 @nutcracker
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy
 *  of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 * Description
 *
 * This driver provides a "notification" capability within your HE automation
 * rules that can be sent to your TV (running Android)
 *
 * Pre-requisites:
 *
 * Install "Notifications for Android TV" from the Google Play appstore
 * https://play.google.com/store/apps/details?id=de.cyberdream.androidtv.notifications.google
 *
*/

import java.net.URLEncoder;

def version()
{
    "v0.2"
} 
 
metadata
{
    definition (name: "AndroidTV Notifier Driver", namespace: "uk.co.inorbit.androidtvnotifier", author: "nutcracker", importurl: "https://raw.githubusercontent.com/nutkracker/AndroidTV-Notifier-Driver/main/androidtv_notifier_driver.groovy") 
    {
        capability "Initialize"
        capability "Notification"
    }
    
    preferences 
    {
        input name: "androidTVIP", type: "string", title: "AndroidTV IP", required: true
        input name: "title", type: "string", title: "Name of your notifier", defaultValue: "Hubitat", required: true
        input name: "pos", type: "enum", title: "Screen position", defaultValue: "Top right", required: true, options: ["Top left","Top right","Bottom left","Bottom right","Centre"]
        input name: "font", type: "enum", title: "Font size", defaultValue: "Max", required: true, options: ["Small","Medium","Large","Max"]
        
        //standard logging options
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
      }
}

def logsOff()
{
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable",[value:"false",type:"bool"])
}

def initialize()
{
    if (logEnable) log.debug ("Initialized");

    if (! androidTVIP)
    {
        log.error "Missing AndroidTV IP settings"
        return
    }
    state.Version = version()
}

def installed()
{
    if (logEnable) log.debug("AndroidTV Notifier Driver installed.")
}

def updated() 
{
    if (logEnable) log.debug("AndroidTV Notifier Driver updated.")

    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800,logsOff)    
}

def parse(String response)
{
    if (logEnable) log.debug("parse:" + response)
}

def deviceNotification(text)
{
    if (logEnable) log.debug("sending message " + text)

    def localDevicePort = "7676"
    def screenpos = 2
    def fontsize = 3
    
    switch (pos)
    {
        case "Top left":
            screenpos=3
            break
        case "Top right":
            screenpos=2
            break
        case "Bottom left":
            screenpos=1
            break
        case "Bottom right":
            screenpos=0
            break
        case "Centre":
            screenpos=4
            break
    }
    
    switch (font)
    {
        case "Small":
            fontsize=1
            break
        case "Medium":
            fontsize=0
            break
        case "Large":
            fontsize=2
            break
        case "Max":
            fontsize=3
            break
    }
      
    def path = "/show?title=" + URLEncoder.encode(title, "UTF-8") + "&msg=" + URLEncoder.encode(text, "UTF-8") + "&fontsize=" + fontsize + "&position=" + screenpos
    
    def headers = [:] 
    headers.put("HOST", "${androidTVIP}:${localDevicePort}")

    try {
        def result = new hubitat.device.HubAction
        (
            method: "POST",
            path: path,
            headers: headers
        ) 
        result
    }
    catch (Exception e) {
        log.debug "Exception ${e} on ${hubAction}"
    } 
}
