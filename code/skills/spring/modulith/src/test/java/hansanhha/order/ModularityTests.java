package hansanhha.order;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import hansanhha.ModulithApplication;

class ModularityTests {

    /*
        ApplicationModules.of(...)
        - 애플리케이션 구조 검사
        - 모듈 컨벤션 적용
        - 모듈 분석
     */
    ApplicationModules modules = ApplicationModules.of(ModulithApplication.class);

    /*
        verify() : ArchUnit을 사용하여 모듈 모델 검증 및 분석
        - 모듈간 순환참조, internal 타입 접근 등

        ArchUnit : https://www.archunit.org/
     */
    @Test
    void verifyModularity() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }
}
