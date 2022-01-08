# Features

## Fulfilled Specifications
All the features that were requested in the project specifications were implemented:

* Each user can have their own account (User) with a unique username.
* Users will be able to deposit credits to their account balance to perform certain transactions.
* There are four types of account types:
  * Accounts that can buy games.
  * Accounts that can sell games.
  * Accounts that can buy and sell games.
  * Administrative users that can do the previous users can, with extended functionality.
    * Issue refunds to users that can buy games.
    * Create and delete accounts.  
    * Activate an auction sale that puts all the games on the market on discount.
    * Extended gifting abilities; Can gift any existing game to other users.
    * Extended credit deposit abilities; Can add credit to other accounts.
    * Extending removing game abilities; Can remove games from other account libraries.
  
All user accounts share the following functionality:

* Login, Logout
* Adding credit to their account
* Gifting games to other users.
* Removing games from their account
  * Sellers can remove games they have put up for sale.
  * Buyers can remove games they have bought.

## How to use the software

[Here](https://youtu.be/ikXPvT6r4AE) is a quick video tutorial to supplement the explanation below

Also note that the development team had an issue related to IntelliJ where it would forcefully remove trailing whitespaces
from the daily.txt file. If you are seeing a lot of fatal errors it is likely due to this issue.
To fix it go to ``File > Settings > Editor > General`` and scroll down to the *On Save* section and uncheck the 'remove
trailing spaces' checkbox. Alternatively you can visit [this stack overflow post](https://stackoverflow.com/q/20679842)

The main file that deals with running the backend is called Client.java and is located within the system package. 

When you run the file you will be prompted for input on what you want to do. There are two choices:
1. You want to manually input beginning users into the database so that your daily.txt file can run successfully.
2. You want to run the backend with the transaction codes within the daily.txt files.

Although **(1)** wasn't explicitly required in the project specifications, a member of this development team heard
from Professor Miljanovic (during office hours) that there needed to be a way for the TA's to manually input/create users
to put into your database in order for the code to run properly;

* The system shouldn't have a god user so there isn't any other way to do this.

**Option 1: Manually Adding Users to the Database**

Prerequisites:

You should put all the information about the users you want to add into the database into a file called
``manual_user_input.txt``. The file will be a csv of the format: ``username,userType,initialBalance``.
* For example, if you want to add a user with the name 'Deval', as an administrator, with the initial balance of 
  999,999.99 you would write ``Deval,AA,999999.99`` on one line. If you wanted to add another BuyStandardUser with 
  the name 'Ziyyad', and initial balance of 1,500,000 you would write ``Ziyyad,BS,1500000``.
  * Since the balance of 1,500,000 is not a valid one, the user would not be created, and a message would be displayed
  in the console telling you.
* Keep in mind that you can put anything in this csv but if a line doesn't follow the format described, or any number of
the constraints the 207 Teaching Team has outlined, then it won't create those users, and the console would display so.
* Note: For successful creation of AdminUser, BuyStandardUser, SellStandardUser, and FullStandardUser, the userType field
should be filled with AA, BS, SS, or FS respectively.
  
An example ``manual_user_input.txt`` file would be:
```
Deval,AA,3214.44
rainydays,AA,8901.331
Mike,AA,10234.44
Ziyyad,AA,3313.93
Domi,BS,50.32
Arcane Wonders,SS,42135
BumblyTheCloud,FS,999999.99
```

Next, after choosing this option you will be asked if you want to load in users from previous backend sessions. Whoever
is testing this project will probably not need to do this unless there was a mistake in your ``manual_user_input.txt``
file that you hadn't noticed which prevented one or more users that you had intended to add from being added. 
If you're in this situation and you click 'yes' you may see a lot of UsernameExceptions saying that a username is already
taken since you already ran that same ``manual_user_input.txt`` previously, but you can ignore that (unless you were 
specifically trying to test for this...in which case it works as intended!).

If you don't want to load in users from previous backend sessions, just type in 'no'.

**Option 2 Running the Backend using daily.txt**

Prerequisites:

You should have a daily.txt file in the project directory. 
* If there is no such file and option 2 is ran, you will get a warning that daily.txt does not exist and one will be made 
  automatically.
* If the daily.txt file with transaction codes within it exists, simply choosing this option will execute the backend
without further prompts.

**Manipulating Data For Correctness Tests**

[Click here](https://youtu.be/394d5TERd2c) for a quick video on interacting/manipulating with the User objects directly.

If you make your own test cases and want to access users, make sure to call ``UserLoader.loadUsers()`` to load in 
the users from the previous session before checking users objects directly. ``loadUsers`` works by placing deserialized
user objects within a hashmap. Note that everytime ``UserLoader.loadUsers()`` is called, a new day has started so this
will mean that some user attributes may be reset such as User.creditsAddedToday which describes the amount of credits a
user has added to their accounts in a running session.

To access user objects directly you can use ``User.getAllUsers().get(username)`` if you choose to.

## Design Decisions and Package Overview

### Description
This section discusses the various design decisions that were involved in developing each package in this project.

### Observer Package
Due to the amount of transactions that resulted in the modification of existing objects combined with the feature of 
being able to retain users from past backend sessions, the requirement of having something that kept track of these
changes was evident.

This led to the usage of the Observer design pattern in this project. The general idea was that since the backend is 
user-centric, users would be the observables and there would be a custom observer that took note of these changes. The changes
would be a user:
* Buying or selling a game.
* Depositing more credit into their balance.
* Removing a game from their library.
* Gifting games to other users.
* Being created or deleted.
* Activating an auction sale.

Whenever such a change occurred, the attached observer would be notified. The observers were set up so that instead of 
directly saving one user to the database at a time, they would save the User.allUsers hashmap (which had every user 
indexed by user-name). So every time a change occurred, the observable would notify its attached observer, and the 
observer would then rewrite the hashmap containing all users to a file. This was done through using the java built in 
``serializable``interface which allowed custom objects to be written to files; User objects would implement this 
interface and extend the AbstractObservable class.

Having an observer-observable relationship for Games was also debated but ultimately it was decided that it was better to indirectly keep a database of existing games through
users and their game libraries because:
* The existence of games were entirely tied to users.
  * Making two disjoint databases would fail to acknowledge this relationship
* The only *potential* attribute altering action for Games could be auction sales. However:
  * In the Users Package subsection, it is discussed that auction sales are conducted by reading a static boolean variable
    that is set to true whenever an auction sale is occurring and false otherwise.
  * Because of how this was set up, the price attribute wasn't actually being changed. Hence, it did not make sense to
    observe games directly.

### Users Package
The project specifications provided that there would be a total of four types of users:
* A user type that can buy games
* A user type that can sell games
* A user type that can buy & sell games
* A user type that can buy & sell games along with perform other tasks (outlined in the README).

Since the project specifications also had several more shared functionalities among all users, an abstract User class
that contained these shared functionalities was an obvious choice because it would limit the amount of repeated code. 
The shared functionalities according to the specifications
would be:
* Adding credit balance to one's account
* Removing a game from one's library (whether it be a game that one is selling, or a game that one has purchased)
* Gifting games (that one owns or is selling) to another user.

Note that the removing and gifting game functionalities were not known to the development team from the beginning. At 
first the User class would only host the depositing credit functionality. Regardless, it still made sense to have such a
class because shared functionalities introduced in the future would only need to be created/exist in one class.

**Buyers and Sellers**

For buying and selling functionality, the development team opted to define such function within two interfaces 
(BuyerInterface & SellerInterface), each with default methods. This way, the nuances of how buying and selling of games 
is performed are defined the same way for all user types. 

The original plan was to use two more abstract classes (AbstractBuyer & AbstractSeller) that would work in the same way 
as the interfaces do. The BuyStandardUser and SellStandardUser would inherit from the respective AbstractClasses and 
FullStandardUser would inherit from both BuyStandardUser and SellStandardUser. The problem was that multiple inheritance 
is not supported in Java. Because of this, the development team attempted to find something similar and landed on interfaces.

The interfaces work in almost the same way in that they define their respective behaviors, but since classes are allowed
to implement multiple interfaces, we could still minimize the amount of repeated code by having FullStandardUser 
implement both these interfaces. This choice was further justified by the fact that interfaces are a promise of fulfilled
behavior for any class that implements it.

**Administrative Users**

Administrative users in this system were defined to be able to do anything a regular user may be able to do, along with
added an extended functionality. Because of this, we chose to make an AdminUser class that would extend the functionality
of FullStandardUsers; Allowed us to reduce code repetition and encapsulate privileged transactions to one class such as:
* Creating new users
  * The factory design pattern (UserFactory) was used to implement this feature because it made having the instantiation
    of users in one separate class - increased modularity.
  * It made sense to do this because we had four types of users, and it allowed for expandability if new user types were
    introduced as well.
* Deleting users
  * In order to differentiate existing and non-existent users in the system it was decided that there would be a static 
    hashmap in the abstract User class that kept all existing Users in one place indexed by user-name.
  * This way made it easy to delete, create and find users:
    * Every time a new User is instantiated it is automatically added to this static hashmap.
    * Deleting a user just involves removing the entry with the corresponding user-name and User object from the hasmap.
    * Finding users is also very simple and done instantly by indexing the Hashmap for a user-name; Opposed to an ArrayList
    which would require a traversal.
* Adding credits to another users balance
  * Because of the previously mentioned static hashmap, extending the ability of admins to add credits to other users
    was also made extremely simple.
  * All that was required was overloading User.addCredit and indexing the hashmap for the User object corresponding to 
    the user-name and using the shared addCredit method defined in the abstract User class.
* Gifting games through an admin
  * This is akin to the case of the previous feature; Extending/overloading the functionality of User.gift.
* Removing the games of other accounts
  * This method was also dealt with the same way as addCredit and gifting.
  * As you can see, the static hashmap and the abstract User class came in really handy and helped to prevent a lot of
    code repetition.
* Activating auction sales on all games
  * Admin users had access to a read-only static boolean variable that indicated whether an auction sale was occurring.
  * This made it so that toggling an auction sale only required toggling the boolean variable.
  * Game objects would then read this boolean variable whenever their getPrice method was called in order to return
    the correct price.

**Loading Users**

Loading users from previous backend sessions required a type of database which the observer package took care of. The
class that took care of loading in users is ``UserLoader`` and it worked as follows:
* Read the serialized hashmap saved by the observer package in the text file located in the ``db`` directory.
* Deserialize the hashmap and the objects within it.
* Individually load all users in the deserialized hashmap into the hashmap that kept track of all created users 
  (User.allUsers).
  
By doing this, it would ensure that every time the backend was executed the client had the choice of loading in users from
previous backend sessions or start from an entirely new (initially empty) database of users. 

It was decided that having such a task delegated to one stand-alone class would increase the modularity of the overall
system. It was debated whether this functionality should have been put into the observer package but ultimately that
didn't seem like good design as it's not something that fits the scenario of observer-observable; observables can't load
themselves in and observers shouldn't be loading observables in as that would violate the observer-observable relationship
since observers would be interfering with the creation of observables.

### Exceptions Package
This package contains all the possible additional developer defined exceptions that may be thrown. There were no special
design decisions that went into creating this package aside from some inheritance.

The specifications defined two types of errors:
* Constraint Errors: Caused when constraints defined for a transaction are not followed.
  * The team defined an abstract exception called ConstraintException.
  * All constraint based exceptions would extend this abstract class: BalanceException, DailyCreditLimitException,
    InvalidGameException, UsernameException, UserAccessException.
  * Attempts were made to make the child constraint exceptions as general as possible so that they could be used in
    several places but with messages that would be more specific to explain why it was thrown. An added bonus was that
    there would be fewer types of exceptions to deal with.
   
* Fatal Errors: The transaction codes provided by the front end are invalid.
  * Since the only known cases of fatal errors being produced would be from invalid transaction codes in the daily.txt
    file, the team opted to only define one type of fatal exception as no more were needed.

### Command Package
The team had noticed that there was a clear connection between transactions and how they played into the command design
pattern. In this case a transaction would be a command/request. 

A command interface was defined with the promise of an ``execute`` method and all the types of commands would implement
this interface. An invoker was also created with the functionality of setting a current command and being able to run it.

The advantages of this were:
* It was easy to treat all command types the same as they all promised a specific behavior, and reduced the need for several 
  if statements.
* Command execution was encapsulated which made the project more modular:
  * When the new transactions of ``gift`` and ``removegame`` were introduced, it was really simple to incorporate into
    the code base; instead of having to modify lines of code in the main client, all that was needed was to create the two
    new command types, and a way to fetch them (discussed in the system package subsection).
* Since the transaction codes were sequential, it was easier to create a simpler queue system when executing the backend.
    
### System Package
This package has a total of five files that work in tandem in order to execute the backend and perform necessary tasks
such as error handling and transaction command fetching:

* ``Client``
  * Takes care of executing the backend by reading from the daily.txt file.
  * Also gives the option of manually inputting users prior to executing the backend (as mentioned by Professor 
    Miljanovic during office hours).
    * This lets the user load in all the necessary Users required to be in the database in order for the backend to 
      properly execute.
* ``DistributionSystem``
  * Mainly keeps track of login and logout transactions by storing the currently logged in user for the invoker
  to use.
* ``ErrorRecorder``
  * Writes all the errors that occur during backend execution to a file with the name 'errorLogs.txt'
* ``TransactionFactory``
  * Uses regular expressions, and the transaction codes from the transaction file (daily.txt) to create and retrieve
  the corresponding command
  * The Factory design pattern was used here for the same reasons as UserFactory; There were eleven types of transactions
  and it made sense to separate the creation/retrieval of commands because of this.
* ``TransactionFile``
  * A class that allowed the system to treat a given daily.txt transaction file as an iterable by applying the iterator
  design pattern.
  * Use of this pattern provided the system with a queue of transaction codes and allowed it to sequentially run each
  valid transaction.

### Game Class
The team couldn't think of a package that suited this class so it was just left in the main directory. This class was 
mainly used to encapsulate the information relating to any given video game in the system. No design pattern in 
particular was used to implement this class as it was almost standalone.

The only notable thing about this class is the way how the overall system was able to use it to distinguish between
the current and previous backend sessions. There is a boolean attribute called offProbation which described whether
actions (such as buying, removing, gifting) could be performed on it. If the attribute was set to true, then the system
would be able to tell that the game was made in a previous backend session. At instantiation, the attribute would originally
be set to false to indicate that it was created in the currently running back end session and this would prevent
actions from being performed on it.

When a new backend session started, UserLoader would simply take care of switching the offProbation attribute to true while
loading in users from the previous backend session.

It was decided to give every game object a non-static version of this attribute so it could be accessed in reference
to a particular Game which made it really easy to implement the functionality of treating each backend session as a different
'day'.