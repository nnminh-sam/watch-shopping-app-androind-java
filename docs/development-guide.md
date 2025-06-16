# App Development Guide

Below is a structured UI Development Guide for your Watch Shopping Android App in Java, detailing:
	•	📅 Development order
	•	📱 Activity names
	•	🧩 Corresponding XML layout files
	•	🎯 Purpose of each screen
	•	🔁 Navigation flow between screens

This plan is designed for efficient delivery in your 3-day timeline, starting from authentication, to product browsing, cart, and checkout.

⸻

📱 UI Development Guide – Watch Shopping App (Java)

⸻

✅ Day 1 – Core User Flow & Product Browsing

1. LoginActivity.java
	•	Layout: activity_login.xml
	•	Purpose: Allow user login using email + password
	•	UI Elements:
	•	EditText: email, password
	•	Button: Login
	•	TextView: Navigate to register
	•	Navigation: → MainActivity on success

⸻

2. RegisterActivity.java
	•	Layout: activity_register.xml
	•	Purpose: Allow new users to register
	•	UI Elements:
	•	EditText: name, email, phone, password
	•	Button: Register
	•	TextView: Navigate to login
	•	Navigation: → MainActivity on success

⸻

3. MainActivity.java
	•	Layout: activity_main.xml
	•	Purpose: Hosts BottomNavigation for:
	•	Home
	•	Cart
	•	Orders
	•	Profile
	•	Navigation:
	•	Uses FragmentContainerView with 4 fragments

⸻

4. HomeFragment.java
	•	Layout: fragment_home.xml
	•	Purpose: Show product grid with categories, filter options
	•	UI Elements:
	•	RecyclerView for product list
	•	Spinner or FilterBar
	•	Navigation: → ProductDetailActivity on click

⸻

5. ProductDetailActivity.java
	•	Layout: activity_product_detail.xml
	•	Purpose: Display product detail, images, add to cart/favorites
	•	UI Elements:
	•	ImageSlider
	•	TextView: name, description, price
	•	Button: Add to Cart, Add to Favorites

⸻

✅ Day 2 – Cart, Checkout, Orders

6. CartFragment.java
	•	Layout: fragment_cart.xml
	•	Purpose: Display user’s cart, quantity updates
	•	UI Elements:
	•	RecyclerView for cart items
	•	TextView: Total
	•	Button: Checkout
	•	Navigation: → CheckoutActivity

⸻

7. CheckoutActivity.java
	•	Layout: activity_checkout.xml
	•	Purpose: Enter delivery address and confirm order
	•	UI Elements:
	•	EditText: address, phone
	•	Spinner: payment method (COD)
	•	Button: Place Order
	•	Navigation: → OrdersFragment

⸻

8. OrdersFragment.java
	•	Layout: fragment_orders.xml
	•	Purpose: Show list of user’s past orders
	•	UI Elements:
	•	RecyclerView with order cards (status, total)
	•	Navigation: → OrderDetailActivity (optional)

⸻

9. OrderDetailActivity.java (optional)
	•	Layout: activity_order_detail.xml
	•	Purpose: Display items and status of selected order
	•	UI Elements:
	•	List of order items
	•	Status display
	•	Delivery address

⸻

✅ Day 3 – Profile, Favorites, Settings

10. ProfileFragment.java
	•	Layout: fragment_profile.xml
	•	Purpose: View/edit user profile
	•	UI Elements:
	•	TextView: name, email, phone
	•	Button: Edit, Logout
	•	Navigation: → EditProfileActivity

⸻

11. EditProfileActivity.java
	•	Layout: activity_edit_profile.xml
	•	Purpose: Edit profile info
	•	UI Elements:
	•	EditText: name, phone
	•	Button: Save

⸻

12. FavoritesFragment.java
	•	Layout: fragment_favorites.xml
	•	Purpose: Display user’s favorite products
	•	UI Elements:
	•	RecyclerView of saved favorites
	•	Button: Remove from favorites

⸻

🗺️ Screen Flow Summary

Login → MainActivity (Home/Cart/Orders/Profile Tabs)
 ↳ Home → ProductDetail
 ↳ Cart → Checkout
 ↳ Profile → EditProfile
 ↳ Orders → OrderDetail
 ↳ Profile → Favorites


⸻

📦 Layout Naming Convention

Activity/Fragment	Layout File
LoginActivity	activity_login.xml
RegisterActivity	activity_register.xml
MainActivity	activity_main.xml
HomeFragment	fragment_home.xml
CartFragment	fragment_cart.xml
OrdersFragment	fragment_orders.xml
ProductDetailActivity	activity_product_detail.xml
CheckoutActivity	activity_checkout.xml
ProfileFragment	fragment_profile.xml
EditProfileActivity	activity_edit_profile.xml
FavoritesFragment	fragment_favorites.xml
