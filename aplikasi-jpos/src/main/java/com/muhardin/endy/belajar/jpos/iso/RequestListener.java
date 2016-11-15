package com.muhardin.endy.belajar.jpos.iso;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.springframework.web.client.RestTemplate;

public class RequestListener implements ISORequestListener {

    public boolean process(ISOSource sender, ISOMsg request) {
        try {
            String mti = request.getMTI();
            if ("0800".equals(mti)) {
                ISOMsg response = (ISOMsg) request.clone();
                response.setMTI("0810");
                response.set(39, "00");
                sender.send(response);
                return true;
            }
            
            if("0200".equals(mti)){
                ISOMsg response = (ISOMsg) request.clone();
                response.setMTI("0210");
                
                String nomorAkun = request.getString(102);
                RestTemplate httpClient = new RestTemplate();
                Map<String, Object> hasil = httpClient.getForObject("https://pelatihan-backend.herokuapp.com/api/rekening/"+nomorAkun+"/", HashMap.class);
                
                response.set(39, "00");
                response.set(104, hasil.get("nama").toString());
                response.set(4, hasil.get("saldo").toString());
                
                sender.send(response);
                return true;
            }
            
            return false;
        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
