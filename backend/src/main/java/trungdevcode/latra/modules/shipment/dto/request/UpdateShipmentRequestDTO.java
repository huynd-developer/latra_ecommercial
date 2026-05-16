package trungdevcode.latra.modules.shipment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import trungdevcode.latra.enums.ShipmentStatus;

@Data
public class UpdateShipmentRequestDTO {
    private String carrier;
    private String trackingCode;
    private ShipmentStatus status;
}