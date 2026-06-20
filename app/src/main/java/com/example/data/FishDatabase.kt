package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Entity(tableName = "fish_items")
data class FishItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val tamilName: String,
    val category: String, // e.g., "Marine", "Freshwater", "Shellfish", "Coldwater"
    val description: String,
    val price: Double,
    val availableQuantity: Int,
    val imageResName: String, // "salmon", "tuna", "snapper", "prawn", "lobster", "trout", "octopus"
    val isAvailable: Boolean = true,
    val availabilityStatus: String = "Available" // "Available", "Sold Out", "Not Available"
)

@Entity(tableName = "fish_orders")
data class FishOrder(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val timestamp: Long,
    val fishName: String,
    val quantity: Int,
    val totalPrice: Double,
    val paymentStatus: String, // "Paid (Simulator)", "Completed"
    val transactionRef: String, // Trans_xxxxxx
    val customerEmail: String,
    val customerName: String = "",
    val customerPhone: String = "",
    val shippingAddress: String = ""
)

@Dao
interface FishDao {
    @Query("SELECT * FROM fish_items ORDER BY name ASC")
    fun getAllFishItems(): Flow<List<FishItem>>

    @Query("SELECT * FROM fish_items WHERE id = :id LIMIT 1")
    suspend fun getFishById(id: Int): FishItem?

    @Query("SELECT * FROM fish_items WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name)) LIMIT 1")
    suspend fun getFishByName(name: String): FishItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFish(fishItem: FishItem)

    @Update
    suspend fun updateFish(fishItem: FishItem)

    @Delete
    suspend fun deleteFish(fishItem: FishItem)

    @Query("DELETE FROM fish_items")
    suspend fun deleteAllFish()

    @Query("SELECT * FROM fish_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<FishOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: FishOrder)
}

@Entity(tableName = "customer_accounts")
data class CustomerAccount(
    @PrimaryKey val email: String,
    val name: String,
    val phone: String,
    val passwordHash: String,
    val address: String = ""
)

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customer_accounts WHERE email = :email LIMIT 1")
    suspend fun getCustomerByEmail(email: String): CustomerAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerAccount)

    @Query("SELECT * FROM customer_accounts")
    fun getAllCustomers(): Flow<List<CustomerAccount>>
}

@Database(entities = [FishItem::class, FishOrder::class, CustomerAccount::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fishDao(): FishDao
    abstract fun customerDao(): CustomerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ekkyfish_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.fishDao())
                }
            }
        }

        suspend fun populateDatabase(fishDao: FishDao) {
            val initialFish = listOf(
                FishItem(
                    name = "Trevally",
                    tamilName = "பாறை மீன்",
                    category = "Marine",
                    description = "Vibrant and gamey coastal marine fish. Firm meat with a rich, savory seafood flavor. Ideal for deep frying, baking, or traditional fish curry.",
                    price = 380.0,
                    availableQuantity = 40, // 20 kg in 500g units
                    imageResName = "img_trevally"
                ),
                FishItem(
                    name = "Pomfret",
                    tamilName = "வவ்வால் மீன்",
                    category = "Marine",
                    description = "Luxurious Silver Pomfret. Highly prized delicacy with buttery, soft white flesh and a wonderfully subtle sweet flavor.",
                    price = 650.0,
                    availableQuantity = 30, // 15 kg in 500g units
                    imageResName = "img_pomfret"
                ),
                FishItem(
                    name = "Cobia",
                    tamilName = "நெய்மீன்",
                    category = "Marine",
                    description = "Premium wild Cobia (Black Kingfish). Firm texture with a sweet, clean flavor, high oil content makes it perfect for roasting or grilling.",
                    price = 420.0,
                    availableQuantity = 48, // 24 kg in 500g units
                    imageResName = "img_cobia"
                ),
                FishItem(
                    name = "Prawn",
                    tamilName = "இறால்",
                    category = "Shellfish",
                    description = "Crisp and juicy Giant Black Tiger Prawns. Sourced wild, perfect for garlic-butter frying, roasting, or delicious skewers.",
                    price = 550.0,
                    availableQuantity = 36, // 18 kg in 500g units
                    imageResName = "img_prawn"
                ),
                FishItem(
                    name = "Red Snapper",
                    tamilName = "சங்கரா மீன்",
                    category = "Marine",
                    description = "Vibrant wild Red Snapper. Moist, delicate white meat with a mild and sweet flavor profile, superb for signature whole roasts.",
                    price = 480.0,
                    availableQuantity = 44, // 22 kg in 500g units
                    imageResName = "img_snapper"
                ),
                FishItem(
                    name = "King Fish",
                    tamilName = "வஞ்சரம் மீன்",
                    category = "Marine",
                    description = "Sought-after Seer Fish (King Fish / Vanjaram). Rich meaty texture with intense delicious flavor, best served as thick traditional fried slices.",
                    price = 850.0,
                    availableQuantity = 32, // 16 kg in 500g units
                    imageResName = "img_kingfish"
                ),
                FishItem(
                    name = "Indian Mackerel",
                    tamilName = "கானாங்கெளுத்தி",
                    category = "Marine",
                    description = "Highly nutritious and fresh Indian Mackerel (Bangda). Full-flavored oil-rich fish, perfect for high-fire spices or traditional coastal coconut curry.",
                    price = 260.0,
                    availableQuantity = 60, // 30 kg in 500g units
                    imageResName = "img_mackerel"
                )
            )
            for (fish in initialFish) {
                fishDao.insertFish(fish)
            }
        }
    }
}

class FishRepository(
    private val fishDao: FishDao,
    private val customerDao: CustomerDao
) {
    val allFishItems: Flow<List<FishItem>> = fishDao.getAllFishItems()
    val allOrders: Flow<List<FishOrder>> = fishDao.getAllOrders()
    val allCustomers: Flow<List<CustomerAccount>> = customerDao.getAllCustomers()

    suspend fun insertFish(fishItem: FishItem) {
        fishDao.insertFish(fishItem)
    }

    suspend fun updateFish(fishItem: FishItem) {
        fishDao.updateFish(fishItem)
    }

    suspend fun deleteFish(fishItem: FishItem) {
        fishDao.deleteFish(fishItem)
    }

    suspend fun deleteAllFish() {
        fishDao.deleteAllFish()
    }

    suspend fun insertOrder(order: FishOrder) {
        fishDao.insertOrder(order)
    }

    suspend fun getFishById(id: Int): FishItem? {
        return fishDao.getFishById(id)
    }

    suspend fun getFishByName(name: String): FishItem? {
        return fishDao.getFishByName(name)
    }

    suspend fun getCustomerByEmail(email: String): CustomerAccount? {
        return customerDao.getCustomerByEmail(email)
    }

    suspend fun insertCustomer(customer: CustomerAccount) {
        customerDao.insertCustomer(customer)
    }
}
