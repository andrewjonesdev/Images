package byaj.configs;

/**
 * Created by student on 7/11/17.
 */
import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.Transformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Component
public class CloudinaryConfig {
    private Cloudinary cloudinary;
    @Value("${cloudinary.cloudname}") private String cloud;
    @Autowired
    public CloudinaryConfig(@Value("${cloudinary.apikey}") String key,
                            @Value("${cloudinary.apisecret}") String secret,
                            @Value("${cloudinary.cloudname}") String cloud){
        cloudinary = Singleton.getCloudinary();
        cloudinary.config.cloudName=cloud;
        cloudinary.config.apiSecret=secret;
        cloudinary.config.apiKey=key;
    }
    public Map upload(Object file, Map options){
        try{
            return cloudinary.uploader().upload(file, options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String createUrl(String name, int width, int height, String action, String effect){
        return cloudinary.url()
                .transformation(new Transformation().width(width).height(height).border("2px_solid_black").crop(action).effect(effect))
                .imageTag(name);
    }
    public String createUrlNoFormat(String name, int width, int height, String action, String effect, int border){
       //
        //Take String name and break by "/" and on the 7th you'll have the name Or use array.length-1
        //http://res.cloudinary.com/andrewjonesdev/image/upload/v1499808350/q0jfkcq9gvukrqvsx3gl.jpg
        String[] url = name.split("/");
        return String.format("http://res.cloudinary.com/%s/image/upload/bo_%spx_solid_black,c_%s,e_%s,h_%s,w_%s/%s", cloud, border, action, effect,height, width, url[url.length-1]);
    }
}