package ru.ilnarsoultanov.osmsnapshottest.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.util.TileSystemWebMercator;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.drawing.MapSnapshot;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

import ru.ilnarsoultanov.osmsnapshottest.R;

public class SnapsShotAdapter extends RecyclerView.Adapter<SnapsShotAdapter.TaskViewHolder> {
    private RecyclerView recyclerView;
    private final List<Overlay> mOverlays;
    private final TileSystem mTileSystem = new TileSystemWebMercator();
    final MapTileProviderBase mapTileProvider;
    private int mapWidth, mapHeight;
    private Bitmap bitmap;
    private Context context;


    public SnapsShotAdapter(@NonNull Context context, int mapWidth, int mapHeight){
        this.context = context;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mOverlays = new ArrayList<>();
        this.mapTileProvider = new MapTileProviderBasic(context);

        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(context, mapWidth, mapHeight);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(mapWidth/2, 10);
        this.mOverlays.add(mScaleBarOverlay);

        download();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.snap_shot_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private ImageView mapImage;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            mapImage = itemView.findViewById(R.id.mapImage);
        }

        void bind(){
            mapImage.setImageBitmap(bitmap);
        }
    }

    private void download() {
        MapView mapView = new MapView(context);

        Polyline trackPolyline = new Polyline(mapView);
        trackPolyline.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        trackPolyline.setWidth(5f);
        trackPolyline.setGeodesic(true);
        trackPolyline.getPaint().setStrokeCap(Paint.Cap.ROUND);

        trackPolyline.addPoint(new GeoPoint(55.669095d, 52.319515d));
        trackPolyline.addPoint(new GeoPoint(55.684386, 52.347261));
        trackPolyline.addPoint(new GeoPoint(55.707427, 52.389842));
        trackPolyline.addPoint(new GeoPoint(55.706186, 52.395886));
        trackPolyline.addPoint(new GeoPoint(55.687645, 52.426517));

        double maxLat = 55.669095d;
        double maxLong = 52.319515d;
        double minLat = 55.669095d;
        double minLong = 52.319515d;

        for(GeoPoint polyline : trackPolyline.getPoints()){
            if (maxLat < polyline.getLatitude())
                maxLat = polyline.getLatitude();
            if (maxLong < polyline.getLongitude())
                maxLong = polyline.getLongitude();

            if (minLat > polyline.getLatitude())
                minLat = polyline.getLatitude();
            if (minLong > polyline.getLongitude())
                minLong = polyline.getLongitude();
        }


        //Marker marker = new Marker(new MapView(App.getAppContext()));
        //marker.setPosition(new GeoPoint(55.724668, 52.444936));
        //BoundingBox boundingBox = new BoundingBox(37.0042610168457, -109.045196533203, 31.3321762084961, -114.818359375);
        //BoundingBox boundingBox = trackPolyline.getBounds();//new BoundingBox(55.785027, 52.517940, 55.666325, 52.241977);

        BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);
        GeoPoint center = new GeoPoint(55.729699, 52.431634);

        mOverlays.add(trackPolyline);

        final double zoom = mTileSystem.getBoundingBoxZoom(boundingBox, mapWidth, mapHeight);
        final MapSnapshot mapSnapshot = new MapSnapshot(new MapSnapshot.MapSnapshotable() {
            @Override
            public void callback(final MapSnapshot pMapSnapshot) {
                if (pMapSnapshot.getStatus() != MapSnapshot.Status.CANVAS_OK) {
                    return;
                }
                bitmap = Bitmap.createBitmap(pMapSnapshot.getBitmap());

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, MapSnapshot.INCLUDE_FLAG_UPTODATE, mapTileProvider, mOverlays,
                new Projection(zoom, mapWidth, mapHeight, center, 0, true, true, 0, 0));
        // mMapSnapshots.put(key, mapSnapshot);
        new Thread(mapSnapshot).start(); // TODO use AsyncTask, Executors instead?
    }
}
