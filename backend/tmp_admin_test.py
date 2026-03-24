import json
import http.cookiejar
import urllib.request

# Set up cookie jar to persist cookies between requests.
cookie_jar = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cookie_jar))

# Login via /admin/login
login_url = 'https://smart-internship-skill-matching-portal-2.onrender.com//admin/login'
login_data = json.dumps({'email': 'madalamanikanta38@gmail.com', 'password': 'madalamani@7075'}).encode('utf-8')
login_req = urllib.request.Request(login_url, data=login_data, headers={'Content-Type': 'application/json'})
login_resp = opener.open(login_req)
print('login status:', login_resp.status)
print('login response:', login_resp.read().decode('utf-8'))

# Access /admin
admin_url = 'https://smart-internship-skill-matching-portal-2.onrender.com//admin'
admin_resp = opener.open(admin_url)
print('/admin status:', admin_resp.status)
print('/admin response:', admin_resp.read().decode('utf-8'))

# Access /admin/dashboard
dash_url = 'https://smart-internship-skill-matching-portal-2.onrender.com//admin/dashboard'
dash_resp = opener.open(dash_url)
print('/admin/dashboard status:', dash_resp.status)
print('/admin/dashboard response:', dash_resp.read().decode('utf-8'))
