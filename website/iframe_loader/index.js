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

function iframeLoader() {

  firebase.database().ref('shining-stars/admin-page/auth-links/login-page/').once('value').then(function(snapshot) {


    $(document).ready(function(e) {
      $('iframe').attr('src',snapshot.val());
    });
    //document.getElementById('loader').src = snapshot.val();

  });

}
