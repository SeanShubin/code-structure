# Distinguishing Runtime Dependency Cycles from Metadata References

## Problem Statement

Checking only the constant pool for dependency cycles produces false positives. A class reference in the constant pool does not necessarily indicate a runtime dependency - it may be metadata-only.

**Example:** Sealed classes in Java 17+ create bidirectional constant pool references (parent lists permitted subclasses) but only unidirectional runtime dependencies (subclasses depend on parent).

## Solution: Check Constant Pool Usage

A constant pool entry becomes a **runtime dependency** only when used in specific contexts. You must examine **where** constant pool CLASS_7 entries are referenced.

---

## Metadata-Only References (NOT Runtime Dependencies)

These attributes contain class references that are **metadata** for compilation, reflection, or verification - but don't cause class loading/initialization:

### 1. PermittedSubclasses (Java 17+)
**Purpose:** Lists allowed subclasses for sealed classes

**Structure:**
```
PermittedSubclasses_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    u2 classes[number_of_classes];  // Constant pool CLASS_7 indices
}
```

**Example (from sealed Project class):**
```
attribute[2]: PermittedSubclasses
  numberOfClasses: 2(0x0002)
  classes(2)
    class[0]: com/seanshubin/warden/domain/CodeProject
      classIndex: 6(0x0006)  // Points to constant pool entry
    class[1]: com/seanshubin/warden/domain/GitOnlyProject
      classIndex: 8(0x0008)  // Points to constant pool entry
```

**Why not a runtime dependency:** Subclasses are not loaded when parent loads. Used for exhaustiveness checking and preventing unauthorized subclasses.

---

### 2. InnerClasses
**Purpose:** Documents nested class relationships

**Structure:**
```
InnerClasses_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    {
        u2 inner_class_info_index;  // CLASS_7 index
        u2 outer_class_info_index;  // CLASS_7 index
        u2 inner_name_index;
        u2 inner_class_access_flags;
    } classes[number_of_classes];
}
```

**Why not a runtime dependency:** Used for reflection (Class.getEnclosingClass()) but doesn't force loading.

---

### 3. NestMembers / NestHost (Java 11+)
**Purpose:** Documents nest membership for private access

**Structure:**
```
NestMembers_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    u2 classes[number_of_classes];  // CLASS_7 indices
}

NestHost_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 host_class_index;  // CLASS_7 index
}
```

**Why not a runtime dependency:** Used for access control verification, not initialization.

---

### 4. EnclosingMethod
**Purpose:** Identifies the method containing an anonymous class

**Structure:**
```
EnclosingMethod_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 class_index;  // CLASS_7 index
    u2 method_index; // METHOD_REF or 0
}
```

---

### 5. Signature (Generics)
**Purpose:** Stores generic type information

**Example:**
```
Signature: Lcom/example/Parent<Lcom/example/Child;>;
```

**Why not a runtime dependency:** Type erasure removes this at runtime. Used by compiler/reflection only.

---

### 6. Record (Java 16+)
**Purpose:** Documents record components

**Structure:**
```
Record_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 components_count;
    record_component_info components[components_count];
}
```

Classes referenced in component type signatures are metadata.

---

### 7. Annotations (Runtime/RetentionPolicy.CLASS)
**Locations:**
- RuntimeVisibleAnnotations / RuntimeInvisibleAnnotations
- RuntimeVisibleParameterAnnotations / RuntimeInvisibleParameterAnnotations
- RuntimeVisibleTypeAnnotations / RuntimeInvisibleTypeAnnotations

**Structure:**
```
annotation {
    u2 type_index;  // UTF8 descriptor, may reference classes
    u2 num_element_value_pairs;
    element_value_pairs[num_element_value_pairs];
}
```

**Note:** Class literals in annotations (e.g., `@Anno(MyClass.class)`) appear as class references. These ARE loaded at runtime if annotation is RetentionPolicy.RUNTIME and accessed reflectively. However, RetentionPolicy.CLASS annotations are discarded at runtime.

---

## Runtime Dependencies (TRUE Cycles)

These indicate the class **must be loaded** and potentially **initialized**:

### 1. Bytecode Instructions in Code Attributes

Any CLASS_7 reference used in **method bytecode** creates a runtime dependency.

**Method signature:**
```
method_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
```

**Code attribute structure:**
```
Code_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 max_stack;
    u2 max_locals;
    u4 code_length;
    u1 code[code_length];  // BYTECODE INSTRUCTIONS HERE
    u2 exception_table_length;
    exception_table[exception_table_length];
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
```

**Critical instructions that reference CLASS_7 constants:**

| Opcode | Mnemonic | Description | Triggers Init? |
|--------|----------|-------------|----------------|
| 0xBB | `new` | Create new object | Yes (first use) |
| 0xB2 | `getstatic` | Get static field | Yes (first use) |
| 0xB3 | `putstatic` | Set static field | Yes (first use) |
| 0xB8 | `invokestatic` | Call static method | Yes (first use) |
| 0xC0 | `checkcast` | Type cast check | Load only |
| 0xC1 | `instanceof` | Type instance check | Load only |
| 0xBD | `anewarray` | Create array of objects | Load only |
| 0xC5 | `multianewarray` | Create multidimensional array | Load only |

**Key distinction:**
- **Load only:** Class loaded into JVM but `<clinit>` not run
- **Triggers init:** Class `<clinit>` (static initializer) runs

**Most dangerous for cycles:** Instructions in `<clinit>` methods that trigger initialization of other classes.

---

### 2. Field Declarations

Fields of a class type create a dependency:

```
field_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;  // Type descriptor, e.g., "Lcom/example/OtherClass;"
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
```

**Static fields:** Referenced class loaded when declaring class is initialized (may trigger `<clinit>` cycle).

**Instance fields:** Referenced class loaded when declaring class is loaded (less problematic).

---

### 3. Method Descriptors and Exceptions

**Method descriptor:**
```
descriptor_index: "(Lcom/example/ParamClass;)Lcom/example/ReturnClass;"
```

**Checked exceptions:**
```
Exceptions_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_exceptions;
    u2 exception_index_table[number_of_exceptions];  // CLASS_7 indices
}
```

These cause class loading when the method is invoked, not when the declaring class loads.

---

### 4. Superclass and Interfaces

```
ClassFile {
    ...
    u2 super_class;  // CLASS_7 index (constant pool)
    u2 interfaces_count;
    u2 interfaces[interfaces_count];  // CLASS_7 indices
    ...
}
```

**Always runtime dependencies:** Superclass and interfaces are loaded (and initialized) before subclass.

---

## Implementation Strategy

### Phase 1: Parse Constant Pool
Build a map of constant pool indices to class names.

```
Map<Integer, String> constantPool;
// Parse constants, store CLASS_7 entries
```

### Phase 2: Track References by Context

For each CLASS_7 reference found, categorize it:

```java
enum ReferenceType {
    METADATA_ONLY,           // PermittedSubclasses, InnerClasses, etc.
    RUNTIME_LOAD,            // instanceof, checkcast (loads but doesn't init)
    RUNTIME_INIT,            // new, getstatic, putstatic, invokestatic
    SUPERCLASS_INTERFACE,    // Superclass/interface (always init)
    FIELD_STATIC,            // Static field (init on declaring class init)
    FIELD_INSTANCE           // Instance field (load only)
}
```

### Phase 3: Check Specific Locations

**Algorithm:**
```
For each class file:
  1. Parse superclass/interfaces → RUNTIME_INIT dependencies

  2. Parse fields:
     - Extract type descriptors
     - Categorize as FIELD_STATIC or FIELD_INSTANCE

  3. Parse methods:
     a. For each Code attribute:
        - Parse bytecode instruction stream
        - For each instruction:
          * If opcode in [new, getstatic, putstatic, invokestatic]:
            → RUNTIME_INIT dependency
          * If opcode in [checkcast, instanceof, anewarray]:
            → RUNTIME_LOAD dependency

     b. Track if in <clinit> method (name_index = "<clinit>")
        - Dependencies in <clinit> are most critical for cycles

  4. Parse attributes:
     - PermittedSubclasses → METADATA_ONLY
     - InnerClasses → METADATA_ONLY
     - NestMembers/NestHost → METADATA_ONLY
     - EnclosingMethod → METADATA_ONLY
     - Signature → Extract class references → METADATA_ONLY (unless runtime annotation)
```

### Phase 4: Build Dependency Graph

```
DependencyGraph:
  For each class:
    - Add edges for RUNTIME_INIT dependencies
    - Optional: Add edges for RUNTIME_LOAD (less critical)
    - Ignore METADATA_ONLY
```

### Phase 5: Detect Cycles

Run cycle detection algorithm (e.g., Tarjan's) on the dependency graph.

**Report cycles with context:**
```
Cycle detected:
  ClassA → ClassB (in ClassA.<clinit>, invokestatic)
  ClassB → ClassA (in ClassB.<clinit>, getstatic)
```

---

## Special Case: Static Initializer Cycles

The most problematic cycles occur when:

```
ClassA.<clinit> → references ClassB (getstatic/putstatic/invokestatic/new)
ClassB.<clinit> → references ClassA (getstatic/putstatic/invokestatic/new)
```

This causes **initialization deadlock** at runtime.

**Detection:** Track dependencies specifically within `<clinit>` methods separately.

---

## Testing Strategy

### Test Case 1: Sealed Classes (False Positive)
```kotlin
sealed class Parent
data class Child1(...) : Parent()
data class Child2(...) : Parent()
```

**Expected:**
- Parent constant pool contains Child1, Child2 (in PermittedSubclasses)
- Child1, Child2 constant pools contain Parent (as superclass)
- **Should NOT report cycle** (metadata-only in one direction)

### Test Case 2: Static Initialization Cycle (True Positive)
```kotlin
class ClassA {
    companion object {
        val b = ClassB.value  // getstatic in <clinit>
    }
}

class ClassB {
    companion object {
        val a = ClassA()  // new in <clinit>
    }
}
```

**Expected:**
- ClassA.<clinit> uses getstatic → ClassB
- ClassB.<clinit> uses new → ClassA
- **Should report cycle**

### Test Case 3: Method Parameter Types (False Positive/Low Risk)
```kotlin
class ClassA {
    fun foo(b: ClassB) {}
}

class ClassB {
    fun bar(a: ClassA) {}
}
```

**Expected:**
- Method descriptors reference each other
- **Should NOT report cycle** (loaded on method invocation, not class load)

---

## JVM Class File Format References

**Official Specification:**
- [JVM Spec - Chapter 4: The class File Format](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html)

**Key sections:**
- §4.4: The Constant Pool (structure)
- §4.7: Attributes (all attribute types)
- §4.7.3: The Code Attribute (method bytecode)
- §4.7.31: The PermittedSubclasses Attribute
- §6: The Java Virtual Machine Instruction Set (bytecode opcodes)

**Opcodes:**
- [Table 6.5.newarray-A: Array type codes](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html#jvms-6.5.newarray)
- [All instructions](https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html#jvms-6.5)

---

## Summary: What to Check

| Reference Location | Runtime Dependency? | Check for Cycles? |
|-------------------|---------------------|-------------------|
| Superclass/Interfaces | ✅ Yes (init) | ✅ Yes |
| PermittedSubclasses | ❌ Metadata only | ❌ No |
| InnerClasses | ❌ Metadata only | ❌ No |
| NestMembers/NestHost | ❌ Metadata only | ❌ No |
| EnclosingMethod | ❌ Metadata only | ❌ No |
| Signature (generics) | ❌ Metadata only | ❌ No |
| Static field types | ✅ Yes (load/init) | ✅ Yes |
| Instance field types | ⚠️ Load only | ⚠️ Optional |
| Method descriptors | ⚠️ On invocation | ❌ Usually no |
| Bytecode: new, getstatic, putstatic, invokestatic | ✅ Yes (init) | ✅ Yes (especially in `<clinit>`) |
| Bytecode: checkcast, instanceof, anewarray | ⚠️ Load only | ⚠️ Optional |

**Recommendation:** Focus on bytecode instructions in `<clinit>` methods for the most critical cycles.
