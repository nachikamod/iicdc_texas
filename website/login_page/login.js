var firebaseConfig = {
    apiKey: "AIzaSyBuj7THN1JA_NdzenIY9AHMbJLDeTqmwxc",
    authDomain: "shining-stars-92878.firebaseapp.com",
    databaseURL: "https://shining-stars-92878.firebaseio.com",
    projectId: "shining-stars-92878",
    storageBucket: "shining-stars-92878.appspot.com",
    messagingSenderId: "892344256821",
    appId: "1:892344256821:web:88693014cf0372ecba247c"
};
  // Initialize Firebase
firebase.initializeApp(firebaseConfig);

function login() {

  var email = document.getElementById("email").value;
  var password = document.getElementById("password").value;

  if (ValidateEmail(email)) {
    firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {
      // Handle Errors here.
      var errorCode = error.code;
      var errorMessage = error.message;

      alert(errorCode + "\n" + errorMessage);
    });
  }

  firebase.auth().onAuthStateChanged(function(user){
    if(user) {
      alert("logged in");
    }
  });
}

function ValidateEmail(email) {
  var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;

  if (reg.test(email) == false) {
      alert('Invalid Email Address');
      return false;
  }

  return true;
}
