/*******************************************************************************
 * Copyright 2015 Esri
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 ******************************************************************************/
package com.esri.defense.dse.gpkgviewer.view;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GroupLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geodatabase.Geopackage;
import com.esri.core.geodatabase.GeopackageFeatureTable;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.defense.dse.gpkgviewer.R;
import com.esri.defense.dse.gpkgviewer.util.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GpkgViewerActivity extends ActionBarActivity implements MapActivity {

    private static final String TAG = GpkgViewerActivity.class.getSimpleName();
    private static final int[] LAYER_COLORS = new int[] {
            Color.rgb(255, 0, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(0, 0, 255),
            Color.rgb(255, 255, 0),
            Color.rgb(255, 0, 255),
            Color.rgb(0, 255, 255),
    };

    private GroupLayer exampleLayer = null;
//    private GroupLayer geopackageTestLayer = null;
//    private ArcGISTiledMapServiceLayer basemapLayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpkg_viewer);

        MapView map = getMapView();

        ArcGISRuntime.setClientId("Hu5bsEz4UDdDnsFp");

        AssetManager assets = getAssets();
        try {
            String[] strs = assets.list("");
            String shapefileName = getString(R.string.shapefile_name);
            for (String str : strs) {
                if (str.startsWith(shapefileName)) {
                    Utilities.copyAsset(getAssets(), str, getFilesDir(), false);
                }
            }

            ShapefileFeatureTable shpTable = new ShapefileFeatureTable(new File(getFilesDir(), shapefileName + ".shp").getAbsolutePath());
            FeatureLayer shpLayer = new FeatureLayer(shpTable);
            shpLayer.setRenderer(new SimpleRenderer(new SimpleFillSymbol(Color.GREEN)));
            shpLayer.setOpacity(0.1f);
            shpLayer.setName(shapefileName + " (shapefile basemap)");
            map.addLayer(shpLayer);
        } catch (IOException e) {
            Log.e(TAG, null, e);
        }

//        File file = copyAssetToTemp("newgpkg.gpkg", getAssets());
//        if (null == file) {
//            exampleLayer = null;
//        } else {
//            exampleLayer = createGroupLayerFromGeopackage(file);
//            exampleLayer.setName("newgpkg");
//        }

//        file = copyAssetToTemp("GeopackageTest.gpkg", getAssets());
//        if (null == file) {
//            geopackageTestLayer = null;
//        } else {
//            geopackageTestLayer = createGroupLayerFromGeopackage(file);
//            geopackageTestLayer.setName("GeopackageTest");
//        }

//        basemapLayer = new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/arcgis/rest/services/World_Topo_Map/MapServer");

//        final MapView map = getMapView();
//        map.setOnStatusChangedListener(new OnStatusChangedListener() {
//            @Override
//            public void onStatusChanged(Object o, STATUS status) {
//                if (STATUS.INITIALIZED.equals(status)) {
//                    ((TextView) findViewById(R.id.textView_spatialReference)).setText("Spatial reference: " + map.getSpatialReference().getText());
//                }
//            }
//        });
////        map.addLayer(geopackageTestLayer);
//        map.addLayer(exampleLayer);
////        map.addLayer(basemapLayer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gpk_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_layers) {
            new LayersDialogFragment().show(getFragmentManager(), "LayersDialogFragment");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public MapView getMapView() {
        return (MapView) findViewById(R.id.map);
    }

    private static GroupLayer createGroupLayerFromGeopackage(File gpkgFile) {
        GroupLayer groupLayer = null;
        try {
            Geopackage gpkg = new Geopackage(gpkgFile.getAbsolutePath());
            groupLayer = new GroupLayer(false);
            List<GeopackageFeatureTable> tables = gpkg.getGeopackageFeatureTables();

            //First pass: polygons and unknowns
            HashSet<Geometry.Type> types = new HashSet<>();
            types.add(Geometry.Type.ENVELOPE);
            types.add(Geometry.Type.POLYGON);
            types.add(Geometry.Type.UNKNOWN);
            addTables(groupLayer, tables, types);

            //Second pass: lines
            types.clear();
            types.add(Geometry.Type.LINE);
            types.add(Geometry.Type.POLYLINE);
            addTables(groupLayer, tables, types);

            //Third pass: points
            types.clear();
            types.add(Geometry.Type.MULTIPOINT);
            types.add(Geometry.Type.POINT);
            addTables(groupLayer, tables, types);
        } catch (FileNotFoundException e) {
            Log.d(TAG, null, e);
        } finally {
            return groupLayer;
        }
    }

    private static void addTables(GroupLayer groupLayer, List<GeopackageFeatureTable> tables, Set<Geometry.Type> types) {
        for (GeopackageFeatureTable table : tables) {
            if (types.contains(table.getGeometryType())) {
                String srString = table.getSpatialReference().getText();
                QueryParameters params = new QueryParameters();
                params.setWhere("0 = 0");
                params.setOutFields(new String[]{"*"});
                table.queryFeatures(new QueryParameters(), new CallbackListener<FeatureResult>() {
                    @Override
                    public void onCallback(FeatureResult objects) {
                        long count = objects.featureCount();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, null, throwable);
                    }
                });
                final FeatureLayer layer = new FeatureLayer(table);
                layer.setOnStatusChangedListener(new OnStatusChangedListener() {
                    @Override
                    public void onStatusChanged(Object o, STATUS status) {
                        if (STATUS.INITIALIZED.equals(status)) {
                            Log.d(TAG, "Initialized: " + layer);
                        }
                    }
                });
                groupLayer.addLayer(layer);
            }
        }
    }

}
