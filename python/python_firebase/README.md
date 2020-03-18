##Installing firebase##

Before using firebase functions in any of the program you need to install firebase libraries using pip

So first of all run command in your shell or terminal or command promp (cmd)

```
  pip install requests
  pip install python-firebase
  
```

and thats it now you just need to import firebase from firebase package and read the documentation further on given link

http://ozgur.github.io/python-firebase/

##Note : only follow this commands if you get any errors while compiling

While compiling applications you might get some error like this shown below

```
  
  from .async import process_pool
          ^
SyntaxError: invalid syntax

```

Follow below steps - open your **c drive** 

```

  c:\Users\${your_user_name}\AppData
  
```

Now on this step you might not found the AppData folder cause it's hidden,
to turn on visibility of this folder, in the toolbar **click View**

on right hand side there are three vertical check boxes **Tick Hidden Items**

after this step you will be able to see AppData folder
 again follow the directory (dir) given below
 
```

  c:\Users\${your_user_name}\AppData\Local\Programs\Python\Python${version}\Lib\site-packages\firebase\
   
```

here you will see **async.py** file rename it to **async_.py**

after this renaming, **open __init__.py**  on third line **from .async import process_pool** change this line to

**from .async_ import process_pool** then open firebase.py and repeat the step on 12th line as well.

An thats it you are ready to go with firebase

**Note : if async.py file is not there, you might find file named process_pool.py that means you are getting some different error 
taht means time to get help from google or else me ... LOL**
