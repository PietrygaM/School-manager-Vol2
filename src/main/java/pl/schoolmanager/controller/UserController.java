package pl.schoolmanager.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.schoolmanager.bean.SessionManager;
import pl.schoolmanager.entity.User;
import pl.schoolmanager.repository.UserRepository;
import pl.schoolmanager.repository.UserRoleRepository;
import pl.schoolmanager.validator.EditUsernameValidator;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private UserRoleRepository userRoleRepo;

	// CREATE
	@GetMapping("/create")
	public String createUser(Model m) {
		m.addAttribute("user", new User());
		return "user/new_user"; // view to be developed
	}

	@PostMapping("/create")
	public String createUserPost(@Valid @ModelAttribute User user, BindingResult bindingResult, Model m) {
		if (bindingResult.hasErrors()) {
			return "user/new_user"; // view to be developed
		}
		this.userRepo.save(user);
		return "redirect:/user/all";
	}
	// READ
	@GetMapping("/view/{userId}")
	public String viewUser(Model m, @PathVariable long userId) {
		User user = this.userRepo.findOne(userId);
		m.addAttribute("user", user);
		return "user/show_user"; // view to be developed
	}

	// UPDATE
	@GetMapping("/update/{userId}")
	public String updateUserRole(Model m, @PathVariable long userId) {
		User user = this.userRepo.findOne(userId);
		m.addAttribute("user", user);
		HttpSession session = SessionManager.session();
		session.setAttribute("password", user.getPassword());
		return "user/new_user"; // view to be developed
	}

	@PostMapping("/update/{id}")
	@Transactional
	public String updateUserPost(@Validated(EditUsernameValidator.class) @ModelAttribute User user, BindingResult bindingResult,
			@PathVariable long id) {
		if (bindingResult.hasErrors()) {
			return "user/new_user"; // view to be developed
		}
		HttpSession session = SessionManager.session();
		user.setPasswordEncrypted(session.getAttribute("password").toString());
		session.setAttribute("password", null);
		this.userRepo.save(user);
		this.userRoleRepo.updateWithUsernameByUserId(id, user.getUsername());
		return "redirect:/user/all"; // to decide where to return
	}

	// DELETE
	@GetMapping("/delete/{userId}")
	public String deleteUser(@PathVariable long userId) {
		this.userRepo.delete(userId);
		return "redirect:/user/all"; // to decide where to return
	}

	// SHOW ALL
	@GetMapping("/all")
	public String allUsers(Model m) {
		return "user/all_users";
	}
	
	//Model Attributes
	@ModelAttribute("availableUsers")
	public List<User> getUsers() {
		return this.userRepo.findAll();
	}
	
}
