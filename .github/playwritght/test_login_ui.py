from playwright.sync_api import Page, expect

def test_login_exitoso(page):
    # Pagina de prueba
    page.goto("http://localhost:8080/vista/login.xhtml")
    #Llenar campos de texto con selectores ID=user-name y ID=password
    page.fill("[id='j_idt8:j_idt10']", "julio.dental@dentalpatron.com")
    page.fill("[id='j_idt8:j_idt12']", "julio123")
    #Click en el boton ID=login-button
    page.click("[id='j_idt8:j_idt14']")
    page.wait_for_url("**inicio_emp.xhtml**")
    assert "inicio_emp.xhtml" in page.url

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

