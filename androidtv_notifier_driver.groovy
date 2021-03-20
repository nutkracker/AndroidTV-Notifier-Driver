/*
 *  AndroidTV Notifier Driver    v0.1
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
 */

import java.net.URLEncoder;

def version()
{
    "v0.1"
} 
 
metadata
{
    definition (name: "AndroidTV Notifier Driver", namespace: "uk.co.inorbit.androidtvnotifier", author: "nutcracker") 
    {
        capability "Initialize"
        capability "Notification"
    }
    
    preferences 
    {
        input name: "androidTVIP", type: "string", title: "AndroidTV IP", required: true
        input name: "title", type: "string", title: "Name of your notifier", required: true

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
}

def installed()
{
    if (logEnable) log.debug("AndroidTV Notifier Driver installed.")
}

def updated() 
{
    if (logEnable) log.debug("AndroidTV Notifier Driver updated.")

    log.warn "debug logging is: ${logEnable == true}"
    log.warn "description logging is: ${txtEnable == true}"
    if (logEnable) runIn(1800,logsOff)    
}

def parse()
{
    
}

def deviceNotification(text)
{
    //if (logEnable)
    log.debug("sending message " + text)

    def localDevicePort = "7676"
      
    def path = "/show?title=" + (String)title + "&msg=" + URLEncoder.encode(text, "UTF-8") + "&fontsize=3&position=2"
    
    log.debug("encode:" + URLEncoder.encode(text, "UTF-8"))
    
    def headers = [:] 
    headers.put("HOST", "${androidTVIP}:${localDevicePort}")

    try {
        def result = new hubitat.device.HubAction
        (
            method: "POST",
            path: path,  //"/show?title=Foxbury&msg=Test&fontsize=3&position=2",
            headers: headers
        )  
        result
    }
    catch (Exception e) {
        log.debug "Exception ${e} on ${hubAction}"
    } 
}
