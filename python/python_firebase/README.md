## Installing firebase

Before using firebase functions in any of the program you need to install firebase libraries using pip

So first of all run this commands in your shell or terminal or command promp (cmd)

```
  pip install requests
  pip install python-firebase
  
```

and thats it now you just need to import firebase from firebase package and read the documentation further on given link

http://ozgur.github.io/python-firebase/

## Note : only follow this commands if you get any errors while compiling

While compiling applications you might get some error like this shown below

```
  
  from .async import process_pool
          ^
SyntaxError: invalid syntax

```

Follow steps given below

+ open your **c drive** 

```

  c:\Users\${your_user_name}\AppData
  
```

Now on this step you might not found the AppData folder cause it's hidden

+ To turn on visibility of this folder, in file manager go to toolbar **click View**
 
+ on right hand side there are three vertical check boxes **Tick "Hidden Items"**

After this step you will be able to see AppData folder.
again follow the directory (dir) given below.
 
```

  c:\Users\${your_user_name}\AppData\Local\Programs\Python\Python${version}\Lib\site-packages\firebase\
   
```

+ Here you will see "**async.py**" file rename it to "**async_.py**"

+ After renaming, **open "__init__.py"**, on third line "**from .async import process_pool**" change this import to

"**from .async_ import process_pool**"

+ Now open firebase.py and repeat the same step on 12th line as well.

And thats it !! you are ready to go with firebase

**Note : if async.py file is not there, you might find file named process_pool.py that means you are getting some different error 
taht means time to get help from google or else me ... LOL**
