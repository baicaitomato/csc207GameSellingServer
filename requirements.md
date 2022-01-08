# Requirements and Clarifications

**TODO: Add Requirements and clarifications as specified in the README.md**

### Clarifications
**From @675**
* auctionsale has a ``07`` transaction code and uses the same format as login/logout/etc.
* A file may be a good idea to store existing user accounts.

**From @673**
* Sell transactions introduce new games.
* If a refund transaction occurs, and the sellers balance isn't high enough, then it is an invalid transaction
* You can use multiple design patterns.

**From @666**
* 999,999.99 is the max credit that can be in any users balance/wallet.
* A 'new day' occurs when your backend is executed.
* Games are not unique to sellers; multiple sellers can be selling the same game.
* If you are adding credit to your account such that it exceeds the maximum amount allowed, max out the credit
  and print a warning.
* If you have maximum balance and sell a game, your balance doesn't change.
* In sell transactions, the 6 character price is the selling price not including the discount.

**From @678**
* Game names are case-sensitive.
* Whenever sellers put up a game for sale, there are an infinite number of copies.

**From @680**
* A full standard user can't sell games they already own in their inventory.

**From @682**
* Once a game is put up for sale, it cannot be taken down unless the seller account that put it up for sale gets 
  deleted.

**From @751**
* Incorrect transaction codes are fatal errors.

**From @777**
* A constraint error would be if a Buy-standard user attempts to sell a game. 
* A fatal error would be if you read the line 'Hello World' from daily.txt

**From @752**
* Constraint errors are the limitations that pertain to transactions.

**From @742**
* Refund transactions are not restricted by the 1000 add credit maximum (Student Answer).

**From @739**
* In all the transactions that include gamename, the number of characters for the gamename should be 25 instead
of the given length.
  * Ex: The transaction code for a login is formatted as:
    * XX IIIIIIIIIIIIIIIIIII SSSSSSSSSSSSS DDDDD PPPPPP
  * IIIIIIIIIIIIIIIIIII is meant to be the gamename but, the length of this isn't 25.
  * Allow for 25 characters for the gamename.

**From @701**
* When a user tries to deposit more than the daily limit, you should reject the attempt entirely and print a warning for the user.

**From @801**
* Jump to next user when someone try to login but the user is not in database

**From @703**
* "only Admin user can delete other users but not themselves. Admin1 can't delete Admin1 but can delete Admin2."

### Assumptions
**From @673**
* You can assume the daily transaction file is sequential and won't have entries where someone logs in halfway through
  another user's transactions.
* You cannot buy a game that isn't for sale.

**From @688**
* Usernames can contain special characters.
* You can assume a username has more than just whitespace.
* You can assume a username will not end in a whitespace character.

**From @741**
* The admin can assume that the buyer requesting a refund from a seller has actually bought a game from the seller.

**From @712**
* Game names contain special characters
* Seller cannot sell the same game twice
* The sellers set discounts specific to the games they sell, which are then applied once the auctionSale is on

**From @698**
* As soon as the auctionsale is enabled, all sales (even those in the same session) should be affected
* "is that possible to have multiple sellers of the game having different discounts or sale price?" Yes

**From @694**
* Constraint errors: Actions made by a user that doesn't have the power to do said action, or the requirements for the action to be made haven't been met
* Admin cannot remove their own accounts
* Privileged action is the action only an admin can do

**From @796**
* fullstandrd user cannot buy the game from theirselves

**From @755**
* the discount in the daily transaction file going to be in the form 90, not 0.9
* Don't make god admin user, it is only for type, the system should exactly NOT NEED for a user

**From @853**
* "Buyers can't request refunds. Only admins can do the refund transaction."

**From @890**
* "Can a Sell only USER receive gifts?""No"

**From @670**
* Accounts are saved

**From @675**
* Stored user accounts to a2-domi-andrew-sunny-tin\db\userStorage

**From @688**
* Usernames can contain special characters

**From @689**
* System is functional without admin 

**From @692**
* When admin toggles sale, all games go up for sale

**From @696**
* There is no refund limit.

**From @697**
* When seller sells a game and their balance is maxed, print a warning and continue.

**From @678**
* When auctionsale is enabled all sales are affected. 
* Multiple sellers that have the same game can have different discounts and sale prices

**From @702**
* If refund results in buyer's balance to be greater than max, do not process

**From @712**
* Sell cannot sell duplicates
* Dsicounts are applied once auctionSale

**From @725**
* Can not have negative balance
* username cannot be empty string

**From @736**
* Deleted user's username can be used by new users

**From @739**
* Game prices cannot be negative
* discount is calucalted in percentages
* max game name is 25 characters

**From @741**
* User cannot purchase games they own
* Admin does not need to check refund requests based on games purchased

**From @751**
* Incorrect transaction code is considered as a fatal error

**From @773**
* Game names cannot start with an empty space


**Regular Expressions and Transaction Codes**

XX UUUUUUUUUUUUUUU TT CCCCCCCCC

AddCredit Regex: ``(?<code>\\d{2}) (?<username>.{15}) (?<type>\\s{2}) (?<credit>\\d{6}\\.\\d{2})``
* All fields are used in this transaction therefore none are left empty/zero'd out.

Example of Valid a Valid AddCredit Code:
```commandline
06 Domi               001000.00
```

Login and Create Regex: ``(?<code>\\d{2}) (?<username>.{15}) (?<type>(FS|AA|BS|SS)) (?<credit>\\d{6}\\.\\d{2})``
* [@965](https://piazza.com/class/kjbjwnmx23xp5?cid=965) indicated that instead of completely ignoring the type and credit fields in the login transaction, you should
validate them and print a warning if there is something off. Because of this, we opted to couple theese two transactions
  together with the same regex.

Examples of Valid Codes:  
```commandline
00 Domi            BS 000050.32
01 rainydays       AA 008901.33
```

Logout, Delete, and Auction Sale Regex: ``(?<code>\\d{2}) (?<username>.{15}) (?<type>\\s{2}) (?<unused>(0{6}\\.0{2}|0{9}))``
* All these transactions ignore the user types and account balance therefore as the README indicated, the account type
  field would be filled with whitespace and the credit field would be all 0's or 000000.00
* [@901](https://piazza.com/class/kjbjwnmx23xp5?cid=901) indicated that in auction sale transactions, if the currently
logged in user doesn't have the same username as the one in the transaction code that a warning should be printed therefore
  it was necessary to include username in producing Auctionsale commands.

Examples of Valid Codes:
```commandline
10 Domi               000000.00
02 Ziyyad             000000.00
07 Deval              000000.00
```

---
XX UUUUUUUUUUUUUUU SSSSSSSSSSSSSSS CCCCCCCCC

Refund Regex: ``(?<code>\\d{2}) (?<buyerUsername>.{15}) (?<sellerUsername>.{15}) (?<credit>\\d{6}\\.\\d{2})``
* All provided fields were required in this case.

Example of a Valid Refund Code: 
```commandline
05 Domi            Arcane Wonders  000005.99
```

---
XX IIIIIIIIIIIIIIIIIIIIIIIII SSSSSSSSSSSSSSS DDDDD PPPPPP

Sell Regex: ``(?<code>\\d{2}) (?<gameName>.{25}) (?<sellerUsername>.{15}) (?<discount>\\d{2}\\.\\d{2}) (?<price>\\d{3}\\.\\d{2})``
* All provided fields were required in this transaction

Example of a Valid Sell Code:
```commandline
03 Onitama                   Arcane Wonders  30.00 005.99
```

---
XX IIIIIIIIIIIIIIIIIIIIIIIII SSSSSSSSSSSSSSS UUUUUUUUUUUUUUU

Buy Regex: ``(?<code>\\d{2}) (?<gameName>.{25}) (?<sellerUsername>.{15}) (?<buyerUsername>.{15})``
* All provided fields were necessary

Example of a Valid Buy Code
```commandline
04 Onitama                   Arcane Wonders  Domi           
```
Note that the trailing whitespace isn't directly visible on the screen but it is meant to be there.

---
XX IIIIIIIIIIIIIIIIIIIIIIIII UUUUUUUUUUUUUUU SSSSSSSSSSSSSSS

Remove Game Regex: ``(?<code>\\d{2}) (?<gameName>.{25}) (?<ownerName>.{15}) (?<receiverName>\\s{15})``
* For remove game, we opted to have the entirety of the recieverName field be whitespace. This is because it does not
make sense for a recieverName to be present during a removeGame transaction since it only deals with one user.

Example of a valid Remove Game Code
```commandline
08 Onitama                   Domi                           
```  
Note that the trailing whitespace isn't visible but it is still there.

Gift Game Regex: ``(?<code>\\d{2}) (?<gameName>.{25}) (?<ownerName>.{15}) (?<receiverName>.{15})``
* All fields were used in the gift transaction therefore none of them were left blank or empty.

Example of Gift Game 
```commandline
09 Onitama                   Arcane Wonders  Mike           
```