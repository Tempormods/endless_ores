package net.minecraft.gametest.framework;

public interface GameTestListener {
    void testStructureLoaded(GameTestInfo pTestInfo);

    void testPassed(GameTestInfo pTestInfo, GameTestRunner p_328578_);

    void testFailed(GameTestInfo pTestInfo, GameTestRunner p_334963_);

    void m_177684_(GameTestInfo p_329777_, GameTestInfo p_335800_, GameTestRunner p_330350_);
}