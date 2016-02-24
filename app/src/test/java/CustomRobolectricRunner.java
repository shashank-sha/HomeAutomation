import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;
import org.robolectric.res.OverlayResourceLoader;
import org.robolectric.res.PackageResourceLoader;
import org.robolectric.res.ResourceLoader;
import org.robolectric.res.ResourcePath;
import org.robolectric.res.RoutingResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vedaprakash on 16/3/15.
 */
public class CustomRobolectricRunner extends RobolectricTestRunner {
    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = 18;

    AndroidManifest mDefaultManifest;
    public CustomRobolectricRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }
    @Override
    /*protected AndroidManifest getAppManifest(Config config) {
        String manifestProperty = System.getProperty("android.manifest");
        String resProperty = System.getProperty("android.resources");
        String assetsProperty = System.getProperty("android.assets");
        AndroidManifest androidManifest = createAppManifest(
                    Fs.fileFromPath(manifestProperty),
                    Fs.fileFromPath(resProperty),
                    Fs.fileFromPath(assetsProperty));
            //androidManifest.setPackageName("com.justyoyo");
        return androidManifest;
    }*/
    protected AndroidManifest getAppManifest(Config config) {
        String appRoot = "app/src/main/";
        String manifestPath = appRoot + "AndroidManifest.xml";
        String resDir = appRoot + "res";
        String assetsDir = appRoot + "assets";
        AndroidManifest manifest = createAppManifest(Fs.fileFromPath(manifestPath),
                Fs.fileFromPath(resDir),
                Fs.fileFromPath(assetsDir));
        mDefaultManifest = manifest;
        return manifest;
    }

    @Override
    protected ResourceLoader createAppResourceLoader(ResourceLoader systemResourceLoader, AndroidManifest appManifest) {
        List<PackageResourceLoader> appAndLibraryResourceLoaders = new ArrayList<PackageResourceLoader>();
        for (ResourcePath resourcePath : appManifest.getIncludedResourcePaths()) {
            appAndLibraryResourceLoaders.add(createResourceLoader(resourcePath));
        }

        /* BEGIN EDIT */
        if(mDefaultManifest != null) {
            ResourcePath rpInjected = new ResourcePath(com.zemosolabs.zetarget.R.class, "com.zemosolabs.zetarget", Fs.fileFromPath("sdk/src/main/res"), Fs.fileFromPath("sdk/src/main/assets"));
            appAndLibraryResourceLoaders.add(createResourceLoader(rpInjected));
            /*rpInjected = new ResourcePath(com.google.android.gms.R.class, "com.google.android.gms", Fs.fileFromPath("app/build/generated/source/r/debug"), mDefaultManifest.getAssetsDirectory());
            appAndLibraryResourceLoaders.add(createResourceLoader(rpInjected));
            rpInjected = new ResourcePath(com.zemosolabs.zetarget.R.class, "com.zemosolabs.zetarget", Fs.fileFromPath("app/build/generated/source/r/debug"), mDefaultManifest.getAssetsDirectory());
            appAndLibraryResourceLoaders.add(createResourceLoader(rpInjected));*/
        }
        /* END EDIT */

        OverlayResourceLoader overlayResourceLoader = new OverlayResourceLoader(appManifest.getPackageName(), appAndLibraryResourceLoaders);

        Map<String, ResourceLoader> resourceLoaders = new HashMap<String, ResourceLoader>();
        resourceLoaders.put("android", systemResourceLoader);
        resourceLoaders.put(appManifest.getPackageName(), overlayResourceLoader);
        return new RoutingResourceLoader(resourceLoaders);
    }
}


