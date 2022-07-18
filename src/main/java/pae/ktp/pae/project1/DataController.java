/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pae.ktp.pae.project1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pae.ktp.pae.project1.exceptions.NonexistentEntityException;

/**
 *
 * @author OZIN
 */
@Controller
public class DataController {
    DataJpaController datactrl = new DataJpaController();
    List<Data> newdata = new ArrayList<>();
    
    @RequestMapping("/main")
    public String getMain(){
        return "menu";
    }
    
    //fungsi untuk menampilkan data dari db ke tabel
    @RequestMapping("/data")
    //@ResponseBody 
    public String getDataKTP(Model model){
        int record = datactrl.getDataCount();
        String result="";
        try{
            newdata = datactrl.findDataEntities().subList(0, record);
        }
        catch(Exception e){result=e.getMessage();}
        model.addAttribute("goData", newdata);
        model.addAttribute("record", record);
        return "database";
        
    }
    
    //fungsi untuk menambahkan data yang ada di halaman index.html
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createData(@RequestParam("foto") MultipartFile file, HttpServletRequest request) throws Exception {
        Data d = new Data();
        
        String noktp = request.getParameter("noktp");
        String nama = request.getParameter("nama");
        String tanggal = request.getParameter("tanggal"); 
        Date tgllahir = new SimpleDateFormat("yyyy-MM-dd").parse(tanggal);
        String alamat = request.getParameter("alamat");
        
        d.setNoktp(noktp);
        d.setNama(nama);
        d.setTgllahir(tgllahir);
        d.setAlamat(alamat);
//      d.setFoto((Base64.getEncoder().encodeToString(file.getBytes()))); 

        datactrl.create(d);
        return "redirect:/data";
    }

    
    //fungsi untuk menghapus data, terhubung dengan method destroy yang ada di JPAController
    @GetMapping(value = "/del/{id}")
    public String deleteData(@PathVariable("id") Integer id) throws NonexistentEntityException {
        DataJpaController d = new DataJpaController();
        d.destroy(id);
        return "redirect:/data";
    }
    
    //fungsi untuk button edit agar berpindah ke halaman edit
    @RequestMapping("/edit/{id}")
    public String updateData(@PathVariable("id") Integer id, Model model) throws Exception{
        Data data = datactrl.findData(id);
        model.addAttribute("godata", data);
        return"editdata";
    }
    
    //fungsi untuk mengedit data yang telah eda
    @PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editData(@RequestParam("foto") MultipartFile file, HttpServletRequest request) throws Exception {
        Data d = new Data();
        
        //mendapatkan parameter dari db agar ditampilkan dalam string ketika di web
        String ide = request.getParameter("id");
        String noktp = request.getParameter("noktp");
        String nama = request.getParameter("nama");
        String tanggal = request.getParameter("tanggal"); 
        
        int id = Integer.parseInt(ide);
        d.setId(id);
        d.setNoktp(noktp);
        d.setNama(nama);
        
        Date tgllahir = new SimpleDateFormat("yyyy-MM-dd").parse(tanggal);
        d.setTgllahir(tgllahir);
        String alamat = request.getParameter("alamat");

        d.setAlamat(alamat);

        datactrl.edit(d);
        return "redirect:/data";
    }
}
