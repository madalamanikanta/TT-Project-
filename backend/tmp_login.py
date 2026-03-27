import json
import urllib.request

url = "https://smart-internship-skill-matching-portal.onrender.com//api/auth/login"
data = json.dumps({"email": "madalamanikanta38@gmail.com", "password": "madalamani@7075"}).encode("utf-8")
req = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json"})
resp = urllib.request.urlopen(req)
print(resp.status)
print(resp.read().decode("utf-8"))
