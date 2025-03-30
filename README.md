## Pokémon Illustrated Guide - Android App 

#### Functionality  
1. **Captured Pokémon Display:**  
   - Display a horizontally scrollable list of captured Pokémon.  
   - Sort by capture time (most recent first).  
   - Allow duplicates if the same Pokémon is captured multiple times.  
   - Provide an option to release a Pokémon, removing it from the list.  

2. **Pokémon Collection Display:**  
   - Show a vertically scrollable list of Pokémon categories.  
   - Each category contains a horizontally scrollable list of Pokémon images, grouped by type (e.g., Bug, Dragon, Electric).  
   - Pokémon may appear in multiple categories.  
   - Indicate captured Pokémon with a visual marker (e.g., Pokéball icon).  

3. **Data Handling:**  
   - Store Pokémon data in a local database (SQLite or Room).  
   - Pre-populate the database with Pokémon data.  
   - Allow updating of capture status.  

4. **User Interaction:**  
   - Tap on a Pokémon to view detailed information.  
   - Capture or release Pokémon through interactions.  

#### External Resources  
- **Pokémon Data API:** [PokéAPI](https://pokeapi.co/)  
  - Fetch the first 151 Pokémon:  
    ```
    GET https://pokeapi.co/api/v2/pokemon?limit=151
    ```
  - Fetch individual Pokémon details:  
    ```
    GET https://pokeapi.co/api/v2/pokemon/{id or name}
    ```
  - Fetch Pokémon species details:  
    ```
    GET https://pokeapi.co/api/v2/pokemon-species/{id or name}
    ```

## Architecture overview

The app architecture has three layers: a [data layer](https://developer.android.com/jetpack/guide/data-layer) and a [UI layer](https://developer.android.com/jetpack/guide/ui-layer).

The architecture follows a reactive programming model with [unidirectional data flow](https://developer.android.com/jetpack/guide/ui-layer#udf). With the data layer at the bottom, the key concepts are:

*   Higher layers react to changes in lower layers.
*   Events flow down.
*   Data flows up.

The data flow is achieved using streams, implemented using [Kotlin Flows](https://developer.android.com/kotlin/flow).

## Data layer

The data layer is implemented as an offline-first source of app data and business logic. It is the source of truth for all data in the app.

## UI Layer

The [UI layer](https://developer.android.com/topic/architecture/ui-layer) comprises:

*   UI elements built using [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   [Android ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel)

## Further reading

[Guide to app architecture](https://developer.android.com/topic/architecture)

[Jetpack Compose](https://developer.android.com/jetpack/compose)



The ViewModels receive streams of data from use cases and repositories, and transforms them into UI state. The UI elements reflect this state, and provide ways for the user to interact with the app. These interactions are passed as events to the ViewModel where they are processed.



    
