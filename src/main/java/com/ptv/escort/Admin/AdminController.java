package com.ptv.escort.Admin;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptv.escort.Config.JwtResponse;
import com.ptv.escort.Config.JwtUtil;
import com.ptv.escort.Config.UserVerification;
import com.ptv.escort.User.User;
import com.ptv.escort.User.UserController;
import com.ptv.escort.Utils.FileUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {



    @Autowired
    private AuthenticationManager authenticationmanager;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserVerification myUserDetailsService;

    @Autowired
    private JwtUtil jwttokenutil;



    private static final Logger logger = LoggerFactory.getLogger(UserController.class);



    @RequestMapping(value = "/admin/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User authenticationRequest) throws Exception {

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        if (userDetails == null) {
            throw new RuntimeException("User does not exist.");
        }
        User user = adminService.loginAdmin(authenticationRequest);

        final String jwt = jwttokenutil.generateToken(userDetails);
        logger.info(jwt);
        return ResponseEntity.ok(new JwtResponse(jwt,user));
    }


//    @CrossOrigin(origins = "http://ptvescort.com", maxAge = 3600)
//    @PostMapping("/createescort")
//    public ResponseEntity<?> createEscort(@RequestBody EscortDetails escortDetails){
//        return ResponseEntity.ok(adminService.createNewEscort(escortDetails));
//    }



    @GetMapping("/getallescort")
    public ResponseEntity<?> getListOfAllEscort(){
        return ResponseEntity.ok(adminService.getListOfAllEscorts());
    }



    @PostMapping("/EscortList/WithCategory")
    public ResponseEntity<?> getListOfEscortsWithCategory(@RequestBody EscortDetails details){
        return ResponseEntity.ok(adminService.getListOfEscortsWithCategory(details.getCategory()));
    }


    @RequestMapping(value = "/createescort", method = RequestMethod.POST, consumes = { "multipart/form-data" })
    public EscortDetails saveUser(@RequestParam String name,
                                  @RequestParam String location,
                                  @RequestParam String phoneNumber,
                                  @RequestParam String email,
                                  @RequestParam String category,
                                  @RequestParam String description,
                                 @RequestParam("image") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        EscortDetails savedUser = adminService.createNewEscort(name,location,phoneNumber,email,category,description,fileName);

        String uploadDir = "user-photos/" + savedUser.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return savedUser;
    }

//    @CrossOrigin(origins = "http://ptvescort.com", maxAge = 3600)
//    @GetMapping("/list/all/category/foradmin")
//    public ResponseEntity<?> listAllCategories(){
//        return ResponseEntity.ok(Category.values());
//    }




    @DeleteMapping("/delete/escort/{id}")
    public int deleteEscort(@PathVariable("id") long id){
        return adminService.deleteEscort(id);
    }


    @PostMapping("/get/escort/details/{id}")
    public ResponseEntity<?> getEscortDetails(@PathVariable("id") long id){
        return ResponseEntity.ok(adminService.getEscortDetails(id));

    }



    @PostMapping("/confirm/paymentdetails/for/escort")
    public ResponseEntity<?> getPaymentDetails(@RequestBody ObjectNode user){
        logger.info("WE GOT HERE!!!");
        JsonNode userId = user.get("user");
        long idOfUser = userId.asLong();

        JsonNode escortId = user.get("escort");
        long idOfEscort = escortId.asLong();
     return ResponseEntity.ok(adminService.confirmUserEscortPaymentDetails(idOfUser,idOfEscort));

    }


    @PostMapping("/update/escort/payment")
    public ResponseEntity<?> updatePaymentDetails(@RequestBody ObjectNode user){
        logger.info("WE GOT HERE!!!");
        JsonNode userId = user.get("user");
        long idOfUser = userId.asLong();

        JsonNode escortId = user.get("escort");
        long idOfEscort = escortId.asLong();
        return ResponseEntity.ok(adminService.updateEscortPayment(idOfUser,idOfEscort));
    }

}
