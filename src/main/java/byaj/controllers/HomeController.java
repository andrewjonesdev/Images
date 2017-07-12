package byaj.controllers;

import byaj.configs.CloudinaryConfig;
import byaj.models.*;
import byaj.repositories.*;
import byaj.validators.UserValidator;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by student on 7/10/17.
 */
@Controller
public class HomeController {

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private byaj.services.UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("search", new Search());

        return "base2";
    }

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("search", new Search());
        model.addAttribute("user", new User());
        return "register2";
    }

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        model.addAttribute("search", new Search());

        model.addAttribute("user", user);
        userValidator.validate(user, result);

        if (result.hasErrors()) {
            return "register2";
        } else {
           /* if (user.getRoleSettings().toUpperCase().equals("ADMIN")) {
                //user.setRoles(Arrays.asList(adminRole));
                //userRepository.save(user);
                userService.saveAdmin(user);
            }
            if (user.getRoleSettings().toUpperCase().equals("EMPLOYER")) {
                //user.setRoles(Arrays.asList(employerRole));
                //userRepository.save(user);
                userService.saveEmployer(user);
            }
            if (user.getRoleSettings().toUpperCase().equals("USER")) {
                //user.setRoles(Arrays.asList(userRole));
                //userRepository.save(user);
                userService.saveUser(user);
                model.addAttribute("message", "User Account Successfully Created");
            }*/
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }
        return "redirect:/";
    }
    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("search", new Search());
        return "login2";
    }


    @PostMapping("/search")
    public String searchForResumes(@Valid Search search, BindingResult bindingResult, Principal principal, Model model){
        if (bindingResult.hasErrors()) {
            System.out.println("search");
            return "redirect:/";
        }
        model.addAttribute("search", new Search());
        search.setSearchUser(userRepository.findByUsername(principal.getName()).getId());
        searchRepository.save(search);
        if(search.getSearchType().toLowerCase().equals("username")){
           // model.addAttribute("posts", postRepository.findAllByPostAuthorOrderByPostDateDesc(search.getSearchValue()));
           // model.addAttribute("follow", new Follow());
            return "postresults2";
        }
       /* if(search.getSearchType().toLowerCase().equals("company")){
            ArrayList<User> result = new ArrayList();
            List<Work> comp = workRepository.findAllByWorkEmployerOrderByWorkResAsc(search.getSearchValue());
            for (int count = 0; count< comp.size(); count++){
                result.add(userRepository.findById(comp.get(count).getWorkRes()));
            }
            model.addAttribute("results", result);
            return "searchResults2";
        }
        if(search.getSearchType().toLowerCase().equals("school")){
            ArrayList<User> result = new ArrayList();
            List<Education> comp = educationRepository.findAllByEduSchoolOrderByEduResAsc(search.getSearchValue());
            for (int count = 0; count< comp.size(); count++){
                result.add(userRepository.findById(comp.get(count).getEduRes()));
            }
            model.addAttribute("results", result);
            return "searchResults2";
        }
        if(search.getSearchType().toLowerCase().equals("jobtitle")){
            ArrayList<User> job = new ArrayList();
            //List<Job> comp = jobRepository.findAllByJobTitleOrderByJobStartYearDesc(search.getSearchValue());
            model.addAttribute("jobs", jobRepository.findAllByJobTitleOrderByJobStartYearDescJobStartMonthDesc(search.getSearchValue()));
            return "jobResults2";
        }*/
        return "redirect:/";
    }

    @GetMapping("/upload")
    public String uploadForm(Model model){
        model.addAttribute("search", new Search());
        return "upload2";
    }
    @PostMapping("/upload")
    public String singleImageUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model){
        model.addAttribute("search", new Search());
        if (file.isEmpty()){
            model.addAttribute("message","Please select a file to upload");
            return "upload2";
        }
        try {
            Map uploadResult =  cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));

            System.out.println(cloudc.createUrl(file.getOriginalFilename(), Integer.parseInt(uploadResult.get("width").toString()), Integer.parseInt(uploadResult.get("height").toString()), "fill", "sepia"));
            System.out.println(uploadResult.get("width").toString());
            System.out.println(uploadResult.get("height").toString());
            System.out.println(cloudc.createUrlNoFormat(uploadResult.get("url").toString(), Integer.parseInt(uploadResult.get("width").toString()), Integer.parseInt(uploadResult.get("height").toString()), "fill", "sepia", 2));
            System.out.println(cloudc.createUrlSuper(uploadResult.get("url").toString(), Integer.parseInt(uploadResult.get("width").toString()), Integer.parseInt(uploadResult.get("height").toString()), "fill", "sepia", 2));
            model.addAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");
            model.addAttribute("imageurl1", uploadResult.get("url"));
        //    model.addAttribute("imageurl", cloudc.createUrlNoFormat(uploadResult.get("url").toString(), Integer.parseInt(uploadResult.get("width").toString()), Integer.parseInt(uploadResult.get("height").toString()), "fill", "sepia", 2));
            //model.addAttribute("imageurl", cloudc.createUrl(file.getOriginalFilename(), Integer.parseInt(uploadResult.get("width").toString()), Integer.parseInt(uploadResult.get("height").toString()), "fill", "sepia"));
        /*master*/    model.addAttribute("imageurl2", cloudc.createUrlSuper(uploadResult.get("url").toString(), Integer.parseInt(uploadResult.get("width").toString()), Integer.parseInt(uploadResult.get("height").toString()), "fill", "sepia", 2));
        /*40x40 sepia cropped*/ model.addAttribute("imageurl3", cloudc.createUrlSuper(uploadResult.get("url").toString(), 40, 40, "pad", "sepia"));
        /*40x40 sepia*/ model.addAttribute("imageurl4", cloudc.createUrlSuper(uploadResult.get("url").toString(), 40, 40, "sepia"));
        /*fixed height width auto*/ model.addAttribute("imageurl5", cloudc.createUrlSuper(uploadResult.get("url").toString(), 100));
        } catch (IOException e){
            e.printStackTrace();
            model.addAttribute("message", "Sorry I can't upload that!");
        }
        return "upload2";
    }




}
