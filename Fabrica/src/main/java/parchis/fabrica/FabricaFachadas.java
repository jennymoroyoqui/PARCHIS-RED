package parchis.fabrica;

import parchis.interfaces.IFachadaSala;
import parchis.salamock.FachadaSalaMock;

/**
 * Fabrica de fachadas. Es la UNICA clase que conoce las implementaciones
 * concretas. ControlSala le pide una IFachadaSala y no sabe cual le dieron.
 *
 * Hito 2A: todavia no existe la fachada real, asi que regresa el mock.
 * En el Hito 2B aqui se cambia para que regrese FachadaSala (la real).
 */
public final class FabricaFachadas {

    private FabricaFachadas() {
    }

    public static IFachadaSala crearFachadaSala() {
        return new FachadaSalaMock();
    }
}
