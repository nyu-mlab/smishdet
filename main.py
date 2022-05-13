from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import requests
import re

class Messages(BaseModel):
    messages : str

app = FastAPI()

@app.post("/smishdet/")
async def create_item(message_list: Messages):
    output = {}
    results = {}
    url_safe_result = {}
    url_list = []
    msg_list = message_list.messages.split("; ")
    links_in_msgs = []
    for msg in msg_list:
        print(msg)

        regex = "[\w\-*]+\.+[\w\-*]+\.[\w\-*/*\w*]+|[\w\-*]+\.+[\w\-*/*\w\-*]+"
        matches = re.findall(regex, msg)
        links_in_msgs.append(matches)
        for x in matches:
            url_list.append({"url": x})
    headers = {'Content-type': 'application/json'}

    r = requests.post('https://safebrowsing.googleapis.com/v4/threatMatches:find?key=<your API key>',
    json= {"client":
          {"clientId": "yourcompanyname",
           "clientVersion": "1.5.2" },
        "threatInfo":
        {  "threatTypes": ["MALWARE", "SOCIAL_ENGINEERING"],
           "platformTypes":    ["WINDOWS"],
           "threatEntryTypes": ["URL"],
           "threatEntries": url_list }
           }, headers = headers)

    safe_api_result = r.json()

    if 'matches' in r.json():
        for result in safe_api_result['matches']:
            url_safe_result[result['threat']['url']] = "Unsafe"

    for entry in url_list:
        if entry['url'] not in url_safe_result.keys():
            url_safe_result[entry['url']] = "Safe"
    for i in range(len(msg_list)):
        js = {}
        for url in links_in_msgs[i]:
            js[url] = url_safe_result[url]
        results[msg_list[i]] = js
    output["results"] = results
    return output
