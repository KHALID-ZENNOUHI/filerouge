package org.dev.filerouge.service.implementation;

import org.dev.filerouge.domain.Class;
import org.dev.filerouge.domain.Level;
import org.dev.filerouge.domain.Program;
import org.dev.filerouge.repository.ClassRepository;
import org.dev.filerouge.repository.LevelRepository;
import org.dev.filerouge.repository.ProgramRepository;
import org.dev.filerouge.repository.SubjectRepository;
import org.dev.filerouge.web.error.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassServiceTest {

    @Mock
    private ClassRepository classRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private ClassService classService;

    private Class testClass;
    private UUID testClassId;
    private Level testLevel;
    private UUID testLevelId;
    private Program testProgram;
    private UUID testProgramId;

    @BeforeEach
    void setUp() {
        // Set up test data
        testClassId = UUID.randomUUID();
        testClass = new Class();
        testClass.setId(testClassId);
        testClass.setName("Test Class");

        testLevelId = UUID.randomUUID();
        testLevel = new Level();
        testLevel.setId(testLevelId);
        testLevel.setName("Test Level");

        testProgramId = UUID.randomUUID();
        testProgram = new Program();
        testProgram.setId(testProgramId);
        testProgram.setDescription("Test Program");
        testProgram.setClasses(new ArrayList<>());
        testProgram.setSubjects(new ArrayList<>());
    }

    @Test
    void save_WithValidClass_ShouldSaveAndReturnClass() {
        // Arrange
        Class classToSave = new Class();
        classToSave.setName("New Class");
        classToSave.setLevel(testLevel);

        when(levelRepository.existsById(testLevelId)).thenReturn(true);
        when(classRepository.existsByName("New Class")).thenReturn(false);
        when(classRepository.save(any(Class.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Class result = classService.save(classToSave);

        // Assert
        assertNotNull(result);
        assertEquals("New Class", result.getName());
        verify(levelRepository, times(1)).existsById(testLevelId);
        verify(classRepository, times(1)).existsByName("New Class");
        verify(classRepository, times(1)).save(classToSave);
    }

    @Test
    void save_WithDuplicateName_ShouldThrowException() {
        // Arrange
        Class classToSave = new Class();
        classToSave.setName("Existing Class");
        classToSave.setLevel(testLevel);

        when(levelRepository.existsById(testLevelId)).thenReturn(true);
        when(classRepository.existsByName("Existing Class")).thenReturn(true);

        // Act & Assert
        assertThrows(ServiceException.DuplicateResourceException.class, () -> {
            classService.save(classToSave);
        });

        verify(levelRepository, times(1)).existsById(testLevelId);
        verify(classRepository, times(1)).existsByName("Existing Class");
        verify(classRepository, never()).save(any(Class.class));
    }

    @Test
    void save_WithNonExistentLevel_ShouldThrowException() {
        // Arrange
        Class classToSave = new Class();
        classToSave.setName("New Class");
        classToSave.setLevel(testLevel);

        when(levelRepository.existsById(testLevelId)).thenReturn(false);

        // Act & Assert
        assertThrows(ServiceException.ResourceNotFoundException.class, () -> {
            classService.save(classToSave);
        });

        verify(levelRepository, times(1)).existsById(testLevelId);
        verify(classRepository, never()).existsByName(anyString());
        verify(classRepository, never()).save(any(Class.class));
    }

    @Test
    void save_WithInvalidName_ShouldThrowException() {
        // Arrange
        Class classToSave = new Class();
        classToSave.setName("");  // Empty name
        classToSave.setLevel(testLevel);

        // Act & Assert
        assertThrows(ServiceException.ValidationException.class, () -> {
            classService.save(classToSave);
        });

        verify(classRepository, never()).existsByName(anyString());
        verify(levelRepository, never()).existsById(any(UUID.class));
        verify(classRepository, never()).save(any(Class.class));
    }

    @Test
    void update_WithValidClass_ShouldUpdateAndReturnClass() {
        // Arrange
        Class classToUpdate = new Class();
        classToUpdate.setId(testClassId);
        classToUpdate.setName("Updated Class");
        classToUpdate.setLevel(testLevel);

        when(classRepository.existsById(testClassId)).thenReturn(true);
        when(levelRepository.existsById(testLevelId)).thenReturn(true);
        when(classRepository.findById(testClassId)).thenReturn(Optional.of(testClass));
        when(classRepository.save(any(Class.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Class result = classService.update(classToUpdate);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Class", result.getName());
        verify(classRepository, times(1)).existsById(testClassId);
        verify(levelRepository, times(1)).existsById(testLevelId);
        verify(classRepository, times(1)).findById(testClassId);
        verify(classRepository, times(1)).save(classToUpdate);
    }

    @Test
    void update_WithNonExistentClass_ShouldThrowException() {
        // Arrange
        Class classToUpdate = new Class();
        classToUpdate.setId(testClassId);
        classToUpdate.setName("Updated Class");

        when(classRepository.existsById(testClassId)).thenReturn(false);

        // Act & Assert
        assertThrows(ServiceException.ResourceNotFoundException.class, () -> {
            classService.update(classToUpdate);
        });

        verify(classRepository, times(1)).existsById(testClassId);
        verify(classRepository, never()).save(any(Class.class));
    }

    @Test
    void findByName_WithExistingName_ShouldReturnClass() {
        // Arrange
        when(classRepository.findByName("Test Class")).thenReturn(Optional.of(testClass));

        // Act
        Class result = classService.findByName("Test Class");

        // Assert
        assertNotNull(result);
        assertEquals(testClassId, result.getId());
        assertEquals("Test Class", result.getName());
        verify(classRepository, times(1)).findByName("Test Class");
    }

    @Test
    void findByName_WithNonExistentName_ShouldThrowException() {
        // Arrange
        when(classRepository.findByName("Non-existent Class")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ServiceException.ResourceNotFoundException.class, () -> {
            classService.findByName("Non-existent Class");
        });

        verify(classRepository, times(1)).findByName("Non-existent Class");
    }

    @Test
    void existsByName_WithExistingName_ShouldReturnTrue() {
        // Arrange
        when(classRepository.existsByName("Test Class")).thenReturn(true);

        // Act
        boolean result = classService.existsByName("Test Class");

        // Assert
        assertTrue(result);
        verify(classRepository, times(1)).existsByName("Test Class");
    }

    @Test
    void existsByName_WithNonExistentName_ShouldReturnFalse() {
        // Arrange
        when(classRepository.existsByName("Non-existent Class")).thenReturn(false);

        // Act
        boolean result = classService.existsByName("Non-existent Class");

        // Assert
        assertFalse(result);
        verify(classRepository, times(1)).existsByName("Non-existent Class");
    }

    @Test
    void findByLevelId_ShouldReturnClassesList() {
        // Arrange
        List<Class> classes = Collections.singletonList(testClass);

        when(levelRepository.existsById(testLevelId)).thenReturn(true);
        when(levelRepository.findById(testLevelId)).thenReturn(Optional.of(testLevel));
        when(classRepository.findByLevel(testLevel)).thenReturn(classes);

        // Act
        List<Class> result = classService.findByLevelId(testLevelId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testClassId, result.get(0).getId());
        verify(levelRepository, times(1)).existsById(testLevelId);
        verify(levelRepository, times(1)).findById(testLevelId);
        verify(classRepository, times(1)).findByLevel(testLevel);
    }

    @Test
    void findByLevelId_WithPagination_ShouldReturnPageOfClasses() {
        // Arrange
        List<Class> classes = Collections.singletonList(testClass);
        Page<Class> classesPage = new PageImpl<>(classes);
        Pageable pageable = PageRequest.of(0, 10);

        when(levelRepository.existsById(testLevelId)).thenReturn(true);
        when(levelRepository.findById(testLevelId)).thenReturn(Optional.of(testLevel));
        when(classRepository.findByLevel(eq(testLevel), any(Pageable.class))).thenReturn(classesPage);

        // Act
        Page<Class> result = classService.findByLevelId(testLevelId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testClassId, result.getContent().get(0).getId());
        verify(levelRepository, times(1)).existsById(testLevelId);
        verify(levelRepository, times(1)).findById(testLevelId);
        verify(classRepository, times(1)).findByLevel(eq(testLevel), any(Pageable.class));
    }

    @Test
    void findByDepartmentId_ShouldReturnClassesList() {
        // Arrange
        UUID departmentId = UUID.randomUUID();
        List<Class> classes = Collections.singletonList(testClass);

        when(classRepository.findByLevelDepartmentId(departmentId)).thenReturn(classes);

        // Act
        List<Class> result = classService.findByDepartmentId(departmentId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testClassId, result.get(0).getId());
        verify(classRepository, times(1)).findByLevelDepartmentId(departmentId);
    }

    @Test
    void countByLevelId_ShouldReturnCount() {
        // Arrange
        when(levelRepository.existsById(testLevelId)).thenReturn(true);
        when(classRepository.countByLevelId(testLevelId)).thenReturn(5L);

        // Act
        long result = classService.countByLevelId(testLevelId);

        // Assert
        assertEquals(5L, result);
        verify(levelRepository, times(1)).existsById(testLevelId);
        verify(classRepository, times(1)).countByLevelId(testLevelId);
    }

    @Test
    void findByProgramId_ShouldReturnClassesList() {
        // Arrange
        List<Class> classes = Collections.singletonList(testClass);

        when(programRepository.existsById(testProgramId)).thenReturn(true);
        when(classRepository.findByProgramId(testProgramId)).thenReturn(classes);

        // Act
        List<Class> result = classService.findByProgramId(testProgramId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testClassId, result.get(0).getId());
        verify(programRepository, times(1)).existsById(testProgramId);
        verify(classRepository, times(1)).findByProgramId(testProgramId);
    }

    @Test
    void findByProgramId_WithPagination_ShouldReturnPageOfClasses() {
        // Arrange
        List<Class> classes = Collections.singletonList(testClass);
        Page<Class> classesPage = new PageImpl<>(classes);

        when(programRepository.existsById(testProgramId)).thenReturn(true);
        when(classRepository.findByProgramId(eq(testProgramId), any(Pageable.class))).thenReturn(classesPage);

        // Act
        Page<Class> result = classService.findByProgramId(testProgramId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testClassId, result.getContent().get(0).getId());
        verify(programRepository, times(1)).existsById(testProgramId);
        verify(classRepository, times(1)).findByProgramId(eq(testProgramId), any(Pageable.class));
    }

    @Test
    void findBySubjectId_ShouldReturnClassesList() {
        // Arrange
        UUID subjectId = UUID.randomUUID();
        List<Class> classes = Collections.singletonList(testClass);

        when(subjectRepository.existsById(subjectId)).thenReturn(true);
        when(classRepository.findByProgramSubjectsId(subjectId)).thenReturn(classes);

        // Act
        List<Class> result = classService.findBySubjectId(subjectId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testClassId, result.get(0).getId());
        verify(subjectRepository, times(1)).existsById(subjectId);
        verify(classRepository, times(1)).findByProgramSubjectsId(subjectId);
    }

    @Test
    void countByProgramId_ShouldReturnCount() {
        // Arrange
        when(programRepository.existsById(testProgramId)).thenReturn(true);
        when(classRepository.countByProgramId(testProgramId)).thenReturn(3L);

        // Act
        long result = classService.countByProgramId(testProgramId);

        // Assert
        assertEquals(3L, result);
        verify(programRepository, times(1)).existsById(testProgramId);
        verify(classRepository, times(1)).countByProgramId(testProgramId);
    }

    @Test
    void assignToProgram_ShouldAssignClassToProgram() {
        // Arrange
        testClass.setProgram(null);  // Class not yet assigned to program

        when(classRepository.findById(testClassId)).thenReturn(Optional.of(testClass));
        when(programRepository.existsById(testProgramId)).thenReturn(true);
        when(programRepository.getReferenceById(testProgramId)).thenReturn(testProgram);
        when(classRepository.save(any(Class.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(programRepository.save(any(Program.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Class result = classService.assignToProgram(testClassId, testProgramId);

        // Assert
        assertNotNull(result);
        assertEquals(testProgram, result.getProgram());
        assertTrue(testProgram.getClasses().contains(result));
        verify(classRepository, times(1)).findById(testClassId);
        verify(programRepository, times(1)).existsById(testProgramId);
        verify(programRepository, times(1)).getReferenceById(testProgramId);
        verify(classRepository, times(1)).save(any(Class.class));
        verify(programRepository, times(1)).save(testProgram);
    }

    @Test
    void removeFromProgram_ShouldRemoveClassFromProgram() {
        // Arrange
        testClass.setProgram(testProgram);
        testProgram.getClasses().add(testClass);

        when(classRepository.findById(testClassId)).thenReturn(Optional.of(testClass));
        when(classRepository.save(any(Class.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(programRepository.save(any(Program.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Class result = classService.removeFromProgram(testClassId);

        // Assert
        assertNotNull(result);
        assertNull(result.getProgram());
        assertFalse(testProgram.getClasses().contains(result));
        verify(classRepository, times(1)).findById(testClassId);
        verify(classRepository, times(1)).save(any(Class.class));
        verify(programRepository, times(1)).save(testProgram);
    }
}