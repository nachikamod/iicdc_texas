from firebase import firebase

firebase = firebase.FirebaseApplication("https://iicdc-texas.firebaseio.com/", None)

firebase.put('/Customer/-M2ie13pR7aykePLkWQd','Name','Yadnyesh Kamod')
print('Record updated')