// IPackageStatsObserver.aidl
package android.content.pm;
import android.content.pm.PackageStats;
// Declare any non-default types here with import statements

//interface IPackageStatsObserver {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   /* void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}*/
 oneway interface IPackageStatsObserver{
     void onGetStatsCompleted(in PackageStats pStats,boolean succeeded);

}
