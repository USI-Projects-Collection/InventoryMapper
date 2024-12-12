package com.example.inventorymapper.ui.gallery;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.inventorymapper.LocationViewModel;
import com.example.inventorymapper.R;
import com.example.inventorymapper.ui.home.HomeViewModel;
import com.example.inventorymapper.ui.model.Household;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.stream.Collectors;

public class MapFragment extends Fragment {
    private MapView map;
    private LocationViewModel locationData;
    private HomeViewModel householdData;
    private Marker userMarker;
    private List<Marker> markerList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_households_map, container, false);
        map = root.findViewById(R.id.map);

        this.locationData = new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        this.householdData = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        Location loc = locationData.getLocation().getValue();
        GeoPoint currentPos = new GeoPoint(loc.getLatitude(), loc.getLongitude());

        householdData.getHouseholds().observe(getActivity(), new Observer<List<Household>>() {
            @Override
            public void onChanged(List<Household> households) {
                if (markerList != null) {
                    map.getOverlays().removeAll(markerList);
                }
                markerList = households.stream().map(household -> {
                    Marker marker = new Marker(map);
                    marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.mapic_house_window));
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setPosition(new GeoPoint(household.getLatitude(), household.getLongitude()));
                    Log.d("Map", String.format("Adding household at %f - %f", household.getLatitude(), household.getLongitude()));
                    return marker;
                }).collect(Collectors.toList());

                map.getOverlays().addAll(markerList);
            }
        });

        userMarker = new Marker(map);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.mapic_person));
        userMarker.setPosition(currentPos);
        map.getOverlays().add(userMarker);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMinZoomLevel(3.0);
        map.setScrollableAreaLimitLatitude(
                MapView.getTileSystem().getMaxLatitude(),
                MapView.getTileSystem().getMinLatitude(),
                0);
        map.setMultiTouchControls(true);
        map.setVerticalMapRepetitionEnabled(false);

        IMapController controller = map.getController();
        controller.setZoom(15.0);
        controller.setCenter(currentPos);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}