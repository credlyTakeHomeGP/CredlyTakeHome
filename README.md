## Architecture
* The app is built using MVI (Model-View-Intent), which is a unidirectional pattern involving two rx streams: one for user interactions and one for states to be rendered. The mediator between these streams is a class defined as StateManager, which accepts user input and outputs view models to be rendered on the UI.
* Use cases (or Interactors) were introduced to encapsulate business logic. These can be directly injected into StateManager.

## Dependency Injection
* Hilt was used for DI given its simplicity to bootstrap and idiomatic APIs.
* StateManager is eagerly instantiated in the class extending Application. 

## Testing
* Mockk was introduced to mock dependencies for making assertions without having to instantiate the actual implementations that are not of concern to what's being tested.
* Turbine was introduced to easily test usage of Kotlin's Flow API.

## Scalability
* The app in this project was treated as a single feature. Introducing new features can involve a similar boilerplate hierarchy in terms of package organization, where every feature is contained in its own module with its own `build.gradle`
* To further ensure robustness, the UI implementation could be changed so that a `FrameLayout` is housed in the `MainActivity`, listening to a state manager; and every screen in the application that hosts a feature can be tied to a `View` that is added to said `FrameLayout`. This would ensure we have a single activity application without having to use additional `Activities` or `Fragments`.
* StateManager is composable. That is, any state manager can consist of "micro" state managers that further specialize on specific features or aspects of the application.
* The app state and user interactions are immutable and trackable. Given that we have a single sources of truth for the app state history and user interaction history, this lends to easy debugging and observability (think Crashlytics/AB-Testing/Business-Intelligence).
* The app does not contain any Espresso tests, though it would be easy to introduce the following pattern: Create a TestPage (or ScreenRobot) class which houses utility methods for asserting on and interacting with elements of a UI rendering. Use said robot/test-page in a test file containing pre-defined narratives for easily-readable tests.
