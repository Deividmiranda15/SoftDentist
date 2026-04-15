import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:8080/vista/login.xhtml"
LOGIN_OK_URL = re.compile(r".*/vista/inicio_emp(?:\.xhtml)?(?:\?.*)?$")


def test_login_correcto(page):
    """Test de login con credenciales correctas"""
    page.goto(BASE_URL)
    page.locator("input[type='text']").first.fill("julio.dental@dentalpatron.com")
    page.locator("input[type='password']").first.fill("julio123")
    page.get_by_role("button", name="Iniciar sesión").click()
    expect(page).to_have_url(LOGIN_OK_URL, timeout=30000)


def test_login_incorrecto(page):
    """Test de login con contraseña incorrecta"""
    page.goto(BASE_URL)
    page.fill("[id='j_idt8:j_idt10']", "julio.dental@dentalpatron.com")
    page.fill("[id='j_idt8:j_idt12']", "incorrecta")
    page.click("[id='j_idt8:j_idt14']")
    expect(page.locator(".ui-growl-message")).to_be_visible()


def test_login_vacio(page):
    """Test de login con campos vacíos"""
    page.goto(BASE_URL)
    page.click("[id='j_idt8:j_idt14']")
    expect(page.locator(".ui-growl-message").first).to_be_visible()


def test_titulo_pagina(page):
    """Test de validación del título de la página"""
    page.goto(BASE_URL)
    assert page.title() == "Iniciar Sesión - Dental Patron"

