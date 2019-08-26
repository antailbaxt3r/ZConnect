# ZConnect_v2 Android coding standards

## Code

#### Firebsae Related
Item Formats (Model classes), Adapters, View Holders must be located in their respective folders. Refer to an exisitng file to know the coding style of each.

#### Whitespace
Code should not have any trailing whitespace to avoid creating unnecessary diff issues. Please setup your IDE to remove these as a save action.

#### Imports
Please setup your IDE to remove all unused imports as a save action.

## XML

#### Structure
XML tags should be ordered as follows: 'xmlns' first, then id, then layout_width and layout_height then alphabetically. 

Add a space between the closing slash and the final attribute. E.g. ```android:textSize="10dp" />```

#### IDE Auto format

Automatic formatting rules for our coding standards are stored in an Android Studio file inside the root of this repository. Please use File > Import Settings and select "android_studio_settings.jar" to ensure you get the same rules.

#### Id names
Layout resource ids should use the following naming convention where possible:<br/>
```<layout name>_<object type>_<object name>```<br/>
E.g.
```
home_listview_hotels
hotel_item_imageview_star_rating
```

#### Example
Given a layout called profile.xml:
```
<?xml  version="1.0"  encoding="utf-8"?> 
<LinearLayout 
    xmlns:android="http://schemas.android.com/apk/res/android" 
    android:layout_width="match_parent" 
    android:layout_height="match_parent"
    android:orientation="vertical" > 

    <!-- Avatar icon -->
    <ImageView
        android:id="@+id/profile_imageview_avatar" 
        android:layout_width="wrap_content" 
        android:layout_height="wrap_content" />
</LinearLayout> 
```

## Documentation

#### Javadoc
Any new classes that are committed must include a class descriptor Javadoc along with:
```@author name@address.com```
Javadoc any public methods, variables and constants. Javadoc private methods where beneficial.

#### Comments
Use in-line commenting to help the next developer who might be editing your code, even if it seems obvious now. Inline comments should appear on the line above the code your are commenting.
Comment XML View elements using ```<!-- Comment -->```.
