package views.hotelveiw.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HotelData {
    private String hotelName;
    private String hotelAddress;
    private String hotelURL;
    private String hotelDescription;
    private String hotelRaiting;

}
