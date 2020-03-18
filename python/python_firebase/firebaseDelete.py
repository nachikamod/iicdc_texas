from firebase import firebase

firebase = firebase.FirebaseApplication("https://iicdc-texas.firebaseio.com/", None)

firebase.delete('/Customer/','-M2ie13pR7aykePLkWQd')
print('record deleted')