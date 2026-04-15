import re

    page.goto("http://localhost:8080/vista/login.xhtml")
    page.goto("http://localhost:8080/vista/login.xhtml")
BASE_URL = "http://localhost:8080/vista/login.xhtml"
LOGIN_OK_URL = re.compile(r".*/vista/inicio_emp(?:\.xhtml)?(?:\?.*)?$")

    page.click("[id='j_idt8:j_idt14']")
    page.goto(BASE_URL)
    page.locator("input[type='text']").first.fill("julio.dental@dentalpatron.com")
    page.locator("input[type='password']").first.fill("julio123")
    page.get_by_role("button", name="Iniciar sesi\u00f3n").click()
    expect(page).to_have_url(LOGIN_OK_URL, timeout=30000)
    # Pagina de prueba
    page.goto("http://localhost:8080/vista/login.xhtml")
    page.goto(BASE_URL)
    page.locator("input[type='text']").first.fill("julio.dental@dentalpatron.com")
    page.locator("input[type='password']").first.fill("incorrecta")
    page.get_by_role("button", name="Iniciar sesi\u00f3n").click()
    # Pagina de prueba
    page.goto("http://localhost:8080/vista/login.xhtml")
    #Llenar campos de texto con selectores ID=user-name y ID=password
    page.goto(BASE_URL)
    page.get_by_role("button", name="Iniciar sesi\u00f3n").click()
    #Click en el boton ID=login-button
    page.click("[id='j_idt8:j_idt14']")
    page.wait_for_url("**inicio_emp.xhtml**")
    page.goto(BASE_URL)

def test_login_incorrecto(page):
    page.goto("http://localhost:8080/vista/login.xhtml")
    page.fill("[id='j_idt8:j_idt10']", "julio.dental@dentalpatron.com")
    page.fill("[id='j_idt8:j_idt12']", "incorrecta")
    page.click("[id='j_idt8:j_idt14']")
    expect(page.locator(".ui-growl-message")).to_be_visible()

def test_login_vacio(page):
    page.goto("http://localhost:8080/vista/login.xhtml")
    page.click("[id='j_idt8:j_idt14']")
    expect(page.locator(".ui-growl-message").first).to_be_visible()

def test_login_vacio(page):
    page.goto("http://localhost:8080/vista/login.xhtml")
    page.click("[id='j_idt8:j_idt14']")
    expect(page.locator(".ui-growl-message").first).to_be_visible()

def test_titulo_pagina(page):
    page.goto("http://localhost:8080/vista/login.xhtml")
    assert page.title() == "Iniciar Sesión - Dental Patron"

