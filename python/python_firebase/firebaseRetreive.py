from firebase import firebase

firebase = firebase.FirebaseApplication("https://iicdc-texas.firebaseio.com/", None)

result = firebase.get('/Customer','')
print(result)