package AlzAware.AlzAware_App.mapper;

import AlzAware.AlzAware_App.dto.GeofenceDTO;
import AlzAware.AlzAware_App.models.Geofence;
import org.springframework.stereotype.Service;

@Service
public class GeofenceToDTO implements TransformService<Geofence, GeofenceDTO> {

    @Override
    public GeofenceDTO toDto(Geofence geofence) {
        GeofenceDTO dto = new GeofenceDTO();
        dto.setId(geofence.getId());
        dto.setLatitude(geofence.getLatitude());
        dto.setLongitude(geofence.getLongitude());
        dto.setRadius(geofence.getRadius());
        dto.setName(geofence.getName());
        dto.setPatientId(geofence.getPatient().getId());
        return dto;
    }
}
