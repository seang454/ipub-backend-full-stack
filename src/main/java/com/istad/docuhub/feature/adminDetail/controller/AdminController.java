package com.istad.docuhub.feature.adminDetail.controller;

import com.istad.docuhub.feature.adminDetail.AdminService;
import com.istad.docuhub.feature.adviserAssignment.AdviserAssignmentServiceImpl;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;
import com.istad.docuhub.feature.adviserAssignment.dto.ReassignAdviserRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.RejectPaperRequest;
import com.istad.docuhub.feature.category.CategoryService;
import com.istad.docuhub.feature.category.dto.CategoryRequest;
import com.istad.docuhub.feature.category.dto.CategoryResponse;
import com.istad.docuhub.feature.paper.PaperService;
import com.istad.docuhub.feature.paper.dto.AdminPaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import com.istad.docuhub.feature.studentDetail.StudentService;
import com.istad.docuhub.feature.studentDetail.dto.RejectStudentRequest;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import com.istad.docuhub.feature.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PaperService paperService;
    private final UserService userService;
    private final AdminService adminService;
    private final StudentService studentService;
    private final AdviserAssignmentServiceImpl adviserAssignmentService;
    private final CategoryService categoryService;

    // normal users
    @GetMapping("users")
    public List<UserResponse> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/public/users")
    public List<UserResponse> getAllPublicUsers() {
        return userService.getAllPublicUser();
    }

    @GetMapping("/user/{uuid}")
    public UserResponse getSingleUser(@PathVariable String uuid) {
        return userService.getSingleUser(uuid);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/user/{uuid}")
    public void deleteUser(@PathVariable String uuid) {
        log.info("User id controller {} ", uuid);
        userService.deleteUser(uuid);
    }


    // student sections
    @GetMapping("/students")
    public List<UserResponse> getAllStudents() {
        return userService.getAllStudent();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/student/create-student")
    public ResponseEntity<String> createStudent(@RequestBody UserCreateDto dto) {
        adminService.createStudent(dto);
        return ResponseEntity.ok("Student created successfully");
    }

    @PostMapping("/student/approve-student-detail")
    public ResponseEntity<?> approveToStudent(@PathVariable String studentUuid) {
        // call pengseang service
        userService.promoteAsStudent(studentUuid);
        return ResponseEntity.ok("Approve student detail successfully");
    }

    @PostMapping("/student/promote")
    public ResponseEntity<?> promoteByAdmin(@PathVariable String userUuid) {
        // call pengseang service
        userService.promoteAsStudent(userUuid);
        return ResponseEntity.ok("Approve student detail successfully");
    }

    @PostMapping("/student/reject-student-detail")
    public ResponseEntity<?> rejectStudentDetail(@RequestBody RejectStudentRequest rejectRequest) {
        studentService.rejectStudentDetail(rejectRequest);
        return ResponseEntity.ok("Reject student detail successfully");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/student/{uuid}")
    public void deleteStudentByUuid(@PathVariable String uuid) {
        log.info("User id controller {} ", uuid);
        userService.deleteUser(uuid);
    }


    // adviser section
    @GetMapping("/advisers")
    public List<UserResponse> getAllMentors() {
        return userService.getAllMentor();
    }

    @PostMapping("/adviser/create-adviser")
    public ResponseEntity<String> createAdviser(@RequestBody UserCreateDto dto) {
        adminService.createAdviser(dto);
        return ResponseEntity.ok("Adviser created successfully");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/adviser/{uuid}")
    public void deleteAdviserByUuid(@PathVariable String uuid) {
        log.info("User id controller {} ", uuid);
        userService.deleteUser(uuid);
    }


    // adviser assignment
    @PostMapping("/paper/assign-adviser")
    public ResponseEntity<AdviserAssignmentResponse> assignAdviser(
            @Valid @RequestBody AdviserAssignmentRequest request
    ) {
        AdviserAssignmentResponse response = adviserAssignmentService.assignAdviserToPaper(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/paper/reassign-adviser")
    public ResponseEntity<AdviserAssignmentResponse> reassignAdviser(
            @RequestBody ReassignAdviserRequest request
    ) {
        AdviserAssignmentResponse response = adviserAssignmentService.reassignAdviser(
                request.paperUuid(),
                request.newAdviserUuid(),
                request.adminUuid(),
                request.deadline()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/paper/reject")
    public ResponseEntity<PaperResponse> rejectPaper(@RequestBody RejectPaperRequest rejectRequest) {
        PaperResponse response = adviserAssignmentService.rejectPaperByAdmin(rejectRequest);
        return ResponseEntity.ok(response);
    }

    // Get all assignments of a specific adviser
    @GetMapping("/paper/{adviserUuid}")
    public ResponseEntity<?> getAssignmentsByAdviser(@PathVariable String adviserUuid) {
        return ResponseEntity.ok(
                adviserAssignmentService.getAssignmentsByAdviserUuid(adviserUuid)
        );
    }

    // (Optional) Just get total count
    @GetMapping("/paper/{adviserUuid}/count")
    public ResponseEntity<?> getAssignmentCount(@PathVariable String adviserUuid) {
        int count = adviserAssignmentService.getAssignmentsByAdviserUuid(adviserUuid).size();
        return ResponseEntity.ok(
                Map.of(
                        "adviserUuid", adviserUuid,
                        "totalAssignments", count
                )
        );
    }


    // paper management
    @GetMapping("/papers")
    public ResponseEntity<?> getAllPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getAllPaper(pageable),
                        "message", "All papers retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("/paper/pendings")
    public ResponseEntity<?> getAllPapersIsPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getAllPapersIsPending(pageable),
                        "message", "All pending papers retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("/paper/{uuid}")
    public ResponseEntity<?> getPaperById(@PathVariable String uuid) {
        return new ResponseEntity<>(
                Map.of(
                        "paper", paperService.getPaperById(uuid),
                        "message", "Paper retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("papers/approved")
    public Page<PaperResponse> getAllPapersIsApproved(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return paperService.getAllPapersIsApproved(pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("paper/{uuid}")
    public void deletePaperById(String uuid) {
        paperService.deletePaperById(uuid);
    }

    @PutMapping("/paper/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: success, no response body
    public void updatePaperByAdmin(
            @PathVariable String uuid,
            @Valid @RequestBody AdminPaperRequest paperRequest
    ) {
        paperService.updatePaperByAdmin(uuid, paperRequest);
    }

    // category management
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(
                Map.of(
                        "message", "Category created successfully"
                ), HttpStatus.CREATED
        );
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategory();
        return ResponseEntity.ok(categories);
    }
}

