package com.example.furni.controllers.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import com.example.furni.entity.Material;
import com.example.furni.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/materials")
    public String showMaterials(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                HttpSession session) {
        Page<Material> materialsPage = materialService.getMaterialsPaginated(page, size);
        model.addAttribute("materialsPage", materialsPage);

        // Lấy thông báo thành công từ session và xóa sau khi lấy
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        return "admin/Material/material";
    }

    @GetMapping("/addMaterial")
    public String showAddMaterialForm(Model model) {
        model.addAttribute("material", new Material());
        return "admin/Material/addMaterial";
    }

    @PostMapping("/addMaterial")
    public String addMaterial(@ModelAttribute("material") Material material, HttpSession session, Model model) {
        // Kiểm tra tính hợp lệ
        if (material.getMaterialName().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Material name cannot be empty.");
            return "admin/Material/addMaterial";
        }

        if (materialService.isMaterialNameExists(material.getMaterialName())) {
            model.addAttribute("errorMessage", "Material name already exists.");
            return "admin/Material/addMaterial";
        }

        materialService.saveMaterial(material);
        session.setAttribute("successMessage", "Material added successfully!");
        return "redirect:/admin/materials";
    }

    @GetMapping("/editMaterial/{id}")
    public String showEditMaterialForm(@PathVariable int id, Model model) {
        Material material = materialService.getMaterialById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        model.addAttribute("material", material);
        return "admin/Material/editMaterial";
    }

    @PostMapping("/editMaterial/{id}")
    public String editMaterial(@PathVariable int id,
                               @ModelAttribute("material") Material materialDetails,
                               HttpSession session, Model model) {
        Material existingMaterial = materialService.getMaterialById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        // Kiểm tra tính hợp lệ
        if (materialDetails.getMaterialName().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Material name cannot be empty.");
            return "admin/Material/editMaterial";
        }

        if (materialService.isMaterialNameExists(materialDetails.getMaterialName(), id)) {
            model.addAttribute("errorMessage", "Material name already exists.");
            return "admin/Material/editMaterial";
        }

        existingMaterial.setMaterialName(materialDetails.getMaterialName());
        materialService.updateMaterial(id, existingMaterial);

        // Thêm thông báo thành công vào session
        session.setAttribute("successMessage", "Material updated successfully!");

        return "redirect:/admin/materials";
    }

    @PostMapping("/deleteMaterial/{id}")
    public String deleteMaterial(@PathVariable int id, HttpSession session) {
        materialService.deleteMaterial(id);
        session.setAttribute("successMessage", "Material deleted successfully!");
        return "redirect:/admin/materials";
    }
}
