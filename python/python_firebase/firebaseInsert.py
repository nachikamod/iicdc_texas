from firebase import firebase

firebase = firebase.FirebaseApplication("https://iicdc-texas.firebaseio.com/", None)
data = {

    'Name' : 'Nachiket Kamod',
    'Email' : 'nachikamod@gmail.com',
    'Phone' : 9405445077,
}

result = firebase.post('/Customer', data)
print(result)