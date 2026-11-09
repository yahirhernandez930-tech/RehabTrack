
package com.rehabtrack.app.data.repository

private data class ContenidoEjercicio(
    val descripcion: String,
    val instrucciones: String
)

object EjercicioCatalogo {

    private val catalogo = mapOf(

        "Sentadilla asistida" to ContenidoEjercicio(
            descripcion = "Ejercicio de movimiento y fortalecimiento que consiste en flexionar parcialmente ambas rodillas y la cadera mientras se utiliza un apoyo para mantener el equilibrio.",
            instrucciones = "1. Colócate de pie frente a una silla o apoyo estable.\n\n2. Sujétate ligeramente y separa los pies al ancho de los hombros.\n\n3. Flexiona lentamente las rodillas llevando la cadera hacia atrás.\n\n4. Regresa de manera controlada a la posición inicial."
        ),

        "Flexión de rodilla" to ContenidoEjercicio(
            descripcion = "Movimiento de flexión de rodilla que consiste en acercar el talón hacia los glúteos mientras se permanece de pie. Trabaja principalmente la musculatura posterior del muslo.",
            instrucciones = "1. Colócate de pie frente a una silla o apoyo estable.\n\n2. Sujétate para mantener el equilibrio.\n\n3. Flexiona una rodilla llevando lentamente el talón hacia los glúteos, sin inclinar el tronco.\n\n4. Regresa lentamente el pie a la posición inicial."
        ),

        "Deslizamiento de talón" to ContenidoEjercicio(
            descripcion = "Ejercicio de movilidad que permite trabajar la flexión de rodilla mediante el deslizamiento del talón sobre una superficie, sin tener que soportar el peso corporal.",
            instrucciones = "1. Acuéstate boca arriba con las piernas extendidas.\n\n2. Mantén el talón en contacto con la superficie.\n\n3. Desliza lentamente el talón hacia los glúteos mientras flexionas la rodilla.\n\n4. Regresa despacio a la posición inicial."
        ),

        "Elevación de talones" to ContenidoEjercicio(
            descripcion = "Ejercicio de fortalecimiento de la musculatura de la pantorrilla que consiste en elevar los talones mientras el peso del cuerpo se mantiene sobre la parte delantera de los pies.",
            instrucciones = "1. Colócate de pie frente a un apoyo estable.\n\n2. Sujétate para mantener el equilibrio.\n\n3. Eleva lentamente ambos talones hasta quedar apoyado sobre la parte delantera de los pies.\n\n4. Desciende los talones de manera controlada."
        ),

        "Elevación de pierna" to ContenidoEjercicio(
            descripcion = "Ejercicio de fortalecimiento que consiste en elevar una pierna manteniendo la rodilla extendida. Trabaja principalmente el cuádriceps y los músculos flexores de la cadera.",
            instrucciones = "1. Acuéstate boca arriba.\n\n2. Mantén una rodilla flexionada y el pie apoyado en la superficie.\n\n3. Conserva la otra pierna extendida.\n\n4. Eleva lentamente la pierna extendida sin doblar la rodilla.\n\n5. Baja de manera controlada hasta la posición inicial."
        ),

        "Extensión de rodilla" to ContenidoEjercicio(
            descripcion = "Ejercicio de fortalecimiento del cuádriceps que consiste en extender la articulación de la rodilla desde una posición sentada, manteniendo el muslo apoyado.",
            instrucciones = "1. Siéntate en una silla con la espalda apoyada.\n\n2. Mantén los pies apoyados en el suelo.\n\n3. Extiende lentamente una rodilla hasta dejar la pierna más recta.\n\n4. Regresa de manera controlada a la posición inicial."
        ),

        "Puente de glúteos" to ContenidoEjercicio(
            descripcion = "Ejercicio de fortalecimiento que consiste en elevar la pelvis desde una posición acostada. Trabaja principalmente los glúteos y la musculatura posterior de las piernas.",
            instrucciones = "1. Acuéstate boca arriba con las rodillas flexionadas y los pies apoyados en el suelo.\n\n2. Mantén los brazos a los lados del cuerpo.\n\n3. Eleva lentamente la pelvis mientras contraes los glúteos.\n\n4. Mantén los hombros apoyados.\n\n5. Desciende la pelvis de manera controlada."
        )
    )

    suspend fun actualizarContenido(
        repository: EjercicioRepository
    ): Int {

        val ejercicios = repository.obtenerTodos()
        var actualizados = 0

        ejercicios.forEach { ejercicio ->

            val contenido = catalogo[ejercicio.nombre]
                ?: return@forEach

            repository.actualizar(
                ejercicio.copy(
                    descripcion = contenido.descripcion,
                    instrucciones = contenido.instrucciones
                )
            )

            actualizados++
        }

        return actualizados
    }
}