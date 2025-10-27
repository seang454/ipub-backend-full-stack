package com.istad.docuhub.feature.adminDetail.controller;

import com.istad.docuhub.domain.User;
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
import com.istad.docuhub.feature.studentDetail.dto.StudentResponse;
import com.istad.docuhub.feature.studentDetail.dto.UpdateStudentRequest;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import com.istad.docuhub.feature.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository;

        // normal user pagination
        @GetMapping("users")
        Page<UserResponse> getAllActiveUsers( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
            return userService.getAllUsersByPage(page, size);
        }

        @GetMapping("/public/users")
        public Map<String, Object> getAllPublicUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
            return userService.getAllPublicUser(PageRequest.of(page,size));
        }
        @GetMapping("/user/{uuid}")
        public UserResponse getSingleUser(@PathVariable String uuid){
            return userService.getSingleUser(uuid);
        }

        @ResponseStatus(HttpStatus.NO_CONTENT)
        @DeleteMapping("/user/{uuid}")
        public void deleteUser(@PathVariable String uuid) {
            userService.deleteUser(uuid);
        }


    // student sections
    @GetMapping("/students")
    public Map<String,Object> getAllStudents(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return userService.getAllStudent(PageRequest.of(page,size));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/student/create-student")
    public ResponseEntity<String> createStudent(@RequestBody UserCreateDto dto) {
        adminService.createStudent(dto);
        return ResponseEntity.ok("Student created successfully");
    }

    @PostMapping("/student/approve-student-detail/{userUuid}")
    public ResponseEntity<?> approveToStudent(@PathVariable String userUuid ) {
        adminService.promoteAsStudent(userUuid);
        return ResponseEntity.ok("Approve student detail successfully");
    }

    @PostMapping("/student/promote/{userUuid}")
    public ResponseEntity<?> promoteByAdmin(@PathVariable String userUuid ) {
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
        log.info("User id controller {} ",uuid);
        userService.deleteUser(uuid);
    }

    // find studentdetail by uuid
    @GetMapping("/student/{userUuid}")
    public StudentResponse findStudentDetailByUserUuid(@PathVariable String userUuid) {
        return studentService.findStudentDetailByUserUuid(userUuid);
    }

    // find all pendings students in pagination
    @GetMapping("student/pending")
    public Page<StudentResponse> getPendingStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 1️⃣ Get all non-deleted users
        List<User> userList = userRepository.getAllUsersByIsDeletedFalse();

        // 2️⃣ Convert to a Set for faster lookup
        Set<String> activeUserUuids = userList.stream()
                .map(User::getUuid)
                .collect(Collectors.toSet());

        // 3️⃣ Get paginated pending students
        Page<StudentResponse> pendingStudentsPage = studentService.findStudentPendingStudents(page, size);

        // 4️⃣ Filter only students whose User is not deleted
        List<StudentResponse> filteredList = pendingStudentsPage.getContent().stream()
                .filter(student -> activeUserUuids.contains(student.userUuid()))
                .toList();

        // 5️⃣ Return new Page object with filtered data
        return new PageImpl<>(
                filteredList,
                PageRequest.of(page, size),
                filteredList.size()
        );
    }


    @PutMapping("student/{userUuid}")
    public ResponseEntity<StudentResponse> updateStudentDetail(
            @PathVariable String userUuid,
            @Valid @RequestBody UpdateStudentRequest updateRequest
    ) {
        StudentResponse updatedStudent = studentService.updateStudentDetailByUserUuid(userUuid, updateRequest);
        return ResponseEntity.ok(updatedStudent);
    }



    // adviser section
    @GetMapping ("/advisers")
    public Map<String,Object> getAllMentors(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return userService.getAllMentor(PageRequest.of(page,size));
    }

    @PostMapping("/adviser/create-adviser")
    public ResponseEntity<String> createAdviser(@RequestBody UserCreateDto dto) {
        adminService.createAdviser(dto);
        return ResponseEntity.ok("Adviser created successfully");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/adviser/{uuid}")
    public void deleteAdviserByUuid(@PathVariable String uuid) {
        userService.deleteUser(uuid);
    }

    @GetMapping("/adviser/{uuid}")
    public UserResponse getAdviserByUuid(@PathVariable String uuid) {
            return userService.getSingleUser(uuid);
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
            @RequestParam(defaultValue = "0") int page,   // page index (0 = first page)
            @RequestParam(defaultValue = "10") int size,  // default 10 items per page
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
                        "message", "All  papers retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("/paper/pendings")
    public ResponseEntity<?> getAllPapersIsPending(
            @RequestParam(defaultValue = "0") int page,   // page index (0 = first page)
            @RequestParam(defaultValue = "10") int size,  // default 10 items per page
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
    public ResponseEntity<?> getAllPapersIsApproved(@RequestParam(defaultValue = "0") int page,   // page index (0 = first page)
                                                    @RequestParam(defaultValue = "10") int size,  // default 10 items per page
                                                    @RequestParam(defaultValue = "createdAt") String sortBy,
                                                    @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getAllPapersIsApproved(pageable),
                        "message", "All approved papers retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/paper/{uuid}")
    public void deletePaperById(@PathVariable String  uuid) {
            paperService.deletePaperById(uuid);
    }

    // update by ton
    @PatchMapping("/paper/{uuid}")
    public ResponseEntity<Map<String, String>> updatePaperPartiallyByAdmin(
            @PathVariable String uuid,
            @RequestBody AdminPaperRequest paperRequest
    ) {
        paperService.updatePaperPartiallyByAdmin(uuid, paperRequest);
        return ResponseEntity.ok(Map.of("message", "Paper updated successfully"));
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


    // adjust shortby createdAt to name in get all cetegories
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,   // 🟢 use an existing field
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CategoryResponse> categories = categoryService.getAllCategory(pageable);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/approved/adviser/paper/{uuid}")
    public ResponseEntity<?> approvedPaperForAdviser(@PathVariable String uuid) {
            adminService.approvedPaperForAdviser(uuid);
            return new ResponseEntity<>(
                    Map.of(
                            "message", "Approved paper for adviser successfully"
                    ), HttpStatus.OK
            );
    }
}

