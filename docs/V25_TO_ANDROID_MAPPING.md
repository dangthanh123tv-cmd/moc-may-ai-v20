# V25 Core -> Android V31 mapping

| V25 Core module | Android V31 target |
|---|---|
| router.py | UnifiedRouter.java |
| memory.py / session.py | ChatStore.java + memory commands |
| database.py / migrations.py | Android SQLite schema/migrations |
| model_manager.py | ModelManager.java |
| mobile.py | DeviceInfo.java + ThermalGuard |
| diagnostics.py | DeviceInfo + /status + CI diagnostics |
| security.py | SecurityPolicy.java + input validation |
| math_normalizer.py | SafeCalculator.java |
| plugins.py | Built-in Java tools only; external plugins remain disabled |
| knowledge.py | Local Knowledge Store (phase 2) |
| knowledge_auto.py | Knowledge ingestion worker (phase 2) |
| web_search.py / web_research.py | Gemini Search grounding through backend (phase 2) |
| updater.py | GitHub release/update checker (phase 3) |
| backup.py | SQLite backup/export (phase 3) |

## Important

Không copy nguyên Python core vào APK. Android V31 chuyển các năng lực cần thiết sang Java/native
để giảm phụ thuộc, giảm RAM và giữ offline-first.

V25 Core vẫn có thể chạy độc lập trong Termux cho các tác vụ quản trị/developer.
