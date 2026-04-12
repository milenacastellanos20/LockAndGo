\#Lock\&Go



!\[Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge\&logo=android\&logoColor=white)

!\[WearOS](https://img.shields.io/badge/WearOS-4285F4?style=for-the-badge\&logo=google\&logoColor=white)

!\[Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge\&logo=kotlin\&logoColor=white)

!\[Jetpack Compose](https://img.shields.io/badge/Compose-4285F4?style=for-the-badge\&logo=jetpackcompose\&logoColor=white)

!\[Room](https://img.shields.io/badge/Room\_DB-00599C?style=for-the-badge\&logo=sqlite\&logoColor=white)



\*\*Lock\&Go\*\* es una aplicación de productividad y bienestar digital diseñada para combatir la procrastinación. Combina el bloqueo activo de aplicaciones en el smartphone con la gamificación y el seguimiento de actividad física a través de un smartwatch (WearOS). 



¿Quieres desbloquear tus redes sociales? Tendrás que ganártelo caminando y cumpliendo tus metas.



\---



\##Características Principales



\* \*\*Bloqueo Activo de Distracciones:\*\* Utiliza un `AccessibilityService` de baja latencia para interceptar y bloquear el acceso a aplicaciones configuradas como distracciones (ej. Instagram, TikTok) mientras haya una sesión activa.

\* \*\*Integración WearOS (Smartwatch):\*\* Sincronización en tiempo real entre el teléfono y el reloj. El WearOS actúa como podómetro en segundo plano, midiendo los pasos necesarios para alcanzar la meta y desbloquear el dispositivo móvil.

\* \*\*Módulo Pomodoro:\*\* Sistema de gestión de tiempo integrado con ciclos de trabajo y descanso para maximizar la concentración.

\* \*\*Gamificación y Recompensas:\*\* Sistema de monedas (`Coins`) que premia al usuario por cumplir sus objetivos diarios y ciclos Pomodoro.

\* \*\*Historial Inmutable:\*\* Base de datos local que registra las metas completadas para que el usuario pueda hacer un seguimiento de su progreso a lo largo del tiempo.



\---



\##Arquitectura y Tecnologías



El proyecto ha sido desarrollado siguiendo los principios de \*\*Clean Architecture\*\* (separación en capas `Domain`, `Data` y `Presentation`) y el patrón de diseño \*\*MVVM\*\* (Model-View-ViewModel).



\* \*\*Lenguaje:\*\* Kotlin

\* \*\*UI:\*\* Jetpack Compose (Interfaz reactiva basada en estados con `StateFlow`)

\* \*\*Persistencia Local:\*\* Room Database (Historial de actividades) y SharedPreferences (Estados de sesión)

\* \*\*Concurrencia:\*\* Kotlin Coroutines \& Flow (`Dispatchers.IO` para DB y procesos en segundo plano)

\* \*\*Comunicación Móvil-Reloj:\*\* Wear OS Data Layer API (`WearableListenerService`)

\* \*\*Servicios del Sistema:\*\* `AccessibilityService` (Bloqueo de apps) y `Foreground Services` (Persistencia del podómetro).



\---



\##Estructura del Proyecto



El repositorio está dividido en dos módulos principales para facilitar su despliegue:

\* `app`: Contiene la aplicación principal para el Smartphone (Android 8.0 Oreo o superior).

\* `wear`: Contiene el módulo específico para el Smartwatch (WearOS 3.0 o superior).



\---



\##Instalación y Despliegue Local



\### Requisitos

\* Android Studio Ladybug (o superior).

\* JDK 17.



\### Pasos para ejecutar

1\. Clona el repositorio:

&#x20;  ```bash

&#x20;  git clone \[https://github.com/milenacastellanos20/LockAndGo](https://github.com/milenacastellanos20/LockAndGo)

2\. Video Explicativo
https://drive.google.com/file/d/139YRc8jNsyC_yXD_P-6ixduSoLQM0-ed/view?usp=sharing

