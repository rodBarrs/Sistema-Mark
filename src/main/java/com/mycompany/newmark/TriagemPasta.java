package com.mycompany.newmark;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TriagemPasta {

    public Chaves_Resultado documentoPasta(WebDriver driver, WebDriverWait wait, Chaves_Configuracao config, String bancos)
            throws InterruptedException, UnsupportedFlavorException, IOException, SQLException {
        LeituraPDF pdf = new LeituraPDF();
        Chaves_Resultado resultado = new Chaves_Resultado();
        //resultado.setEtiqueta("NÃO FOI POSSÍVEL LOCALIZAR FRASE CHAVE ATUALIZADA");
        Triagem_Etiquetas triagem = new Triagem_Etiquetas();
        VerificarData verificarData = new VerificarData();
        Triagem_Condicao condicao = new Triagem_Condicao();
        String linhaMovimentacao = "";
        String condicaoProv = "PRO";
        String condicaoCabecalho = "CAB";
        String localTriagem = "DOC";

        int diferenca = 30;
        boolean existeSeguinte = false;
        int proximaLinha = 0;
        VerificaCondicaoPastas verificaCondicaoPastas = new VerificaCondicaoPastas();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeview-1015")));

        WebElement TabelaTref = driver.findElement(By.id("treeview-1015"));
        List<WebElement> listaMovimentacao = new ArrayList(TabelaTref.findElements(By.cssSelector("tr")));

        int limite = 10;
        WebElement movimentacaoAtual;


        for (int i = listaMovimentacao.size(); i > 0 && limite > 0; i--) {


            Boolean existePasta = driver.findElement(By.xpath("//tr["+i+"]/td[2]/div/img[1]")).getAttribute("class")
                    .contains("x-tree-expander");
            if (existePasta) {
                movimentacaoAtual = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div"));




                if (verificaCondicaoPastas.verificaCondicao(movimentacaoAtual.getText())) {
                    int numeroMovimentacao = Integer.parseInt(driver.findElement(By.xpath(("/html/body/div[2]/div[2]/div/div[2]/div/div[3]/div/table/tbody/tr["+ i +"]/td[1]/div"))).getText());
                    try {
                        existeSeguinte = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]/div/div[3]/div/table/tbody/tr["+ (i+1) +"]/td[1]/div")).getAttribute("class").contains("x-grid-cell");
                        if(existeSeguinte) {
                            int numeroMovimentacaoSeguinte = Integer.parseInt(driver.findElement(By.xpath(("/html/body/div[2]/div[2]/div/div[2]/div/div[3]/div/table/tbody/tr[" + (i + 1) + "]/td[1]/div"))).getText());

                            diferenca = numeroMovimentacaoSeguinte - numeroMovimentacao;
                        }
                    } catch (Exception e){

                    }


                    System.out.println(movimentacaoAtual);
                    System.out.println(numeroMovimentacao);
                   // System.out.println(numeroMovimentacaoSeguinte);



                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("ext-gen1020")));
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[\" + i + \"]/td/div/span/span[1]")));
                    driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/img[1]")).click();
                    if  (!existeSeguinte){
                        WebElement TabelaTref2 = driver.findElement(By.id("treeview-1015"));
                        List<WebElement> listaMovimentacao2 = new ArrayList(TabelaTref.findElements(By.cssSelector("tr")));
                        int numeroUltimaMovimentacao = Integer.parseInt(driver.findElement(By.xpath(("/html/body/div[2]/div[2]/div/div[2]/div/div[3]/div/table/tbody/tr[" + listaMovimentacao2.size() + "]/td[1]/div"))).getText());
                        diferenca = (numeroUltimaMovimentacao - numeroMovimentacao) + 1;

                    }

                        //proximaLinha = Integer
                        //        .parseInt(driver.findElement(By.xpath("//tr[" + (i + 1) + "]/td/div")).getText()) + 1;




                        int contador = 0;
                    for (int j = i ; j < 1000; j++) {


                        String processo = "";
                            pdf.apagarPDF();
                            wait.until(ExpectedConditions
                                    .presenceOfElementLocated(By.xpath("//tr[" + j + "]/td[2]/div/span/span[1]")));
                            wait.until(ExpectedConditions
                                    .elementToBeClickable(By.xpath("//tr[" + j + "]/td[2]/div/span/span[1]")));

                            Thread.sleep(500);

                            wait.until(
                                    ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"ext-gen1124\"]")));

                            // Armazena o span que contém o texto onde diz se o item é um PDF ou HTML
                            String spanText = driver.findElement(By.xpath("//tr[" + j + "]/td[2]/div/span/span[2]"))
                                    .getText().toUpperCase();

                            // Verifica se o documento é um pdf para tratamento apropriado
                            if (spanText.contains("PDF")) {
                                pdf.apagarPDF();
                                // Click na linha
                                driver.findElement(By.xpath("//tr[" + j + "]/td[2]/div/span/span[1]")).click();

                                int cont = 0;
                                while (cont <= 4) {

                                    if (pdf.PDFBaixado()) {
                                        processo = pdf.lerPDF();
                                        break;
                                    } else {
                                        pdf.apagarPDF();
                                        driver.findElement(By.xpath("//tr[" + j + "]/td[2]/div/span/span[1]")).click();
                                    }
                                    cont++;
                                }

                            } else {
                                driver.findElement(By.xpath("//tr[" + j + "]/td[2]/div/span/span[1]")).click();
                                // Envia o driver para o iframe e verifca os itens internos para confirmação do
                                // carregamento
                                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("iframe-myiframe")));
                                WebElement iframe = driver.findElement(By.id("iframe-myiframe"));
                                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
                                // Garante o clique no iframe
                                boolean flag = true;
                                do {
                                    try {
                                        wait.until(ExpectedConditions.elementToBeClickable(By.tagName("html")));
                                        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
                                        driver.findElement(By.tagName("body")).click();
                                        flag = false;
                                    } catch (Exception e) {
                                        // Nothing to do at all
                                    }

                                } while (flag);
                                Actions action = new Actions(driver);
                                action.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0061')).perform();
                                action.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0063')).perform();
                                driver.switchTo().defaultContent();
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                DataFlavor flavor = DataFlavor.stringFlavor;
                                Thread.sleep(500);
                                processo = clipboard.getData(flavor).toString();
                            }

                            if (processo.length() > 1) {

                                //limite--;
                                try {
                                    Boolean identificadoDePeticao = false;
                                    resultado = triagem.triarBanco(processo, bancos, localTriagem, config.getTipoTriagem(),
                                            identificadoDePeticao);
                                    if (!resultado.getEtiqueta()
                                            .contains("NÃO FOI POSSÍVEL LOCALIZAR FRASE CHAVE ATUALIZADA")
                                            && !resultado.getEtiqueta().contains("ERRO EM TRIAGEM: PDF NÃO PESQUISÁVEL")) {
                                        linhaMovimentacao = driver.findElement(By.xpath("//tr[" + i + "]/td/div"))
                                                .getText();
                                        resultado.setLocal("DOC " + linhaMovimentacao);
                                        resultado.setDriver(driver);
                                        return resultado;
                                    }
                                } catch (Exception erro) {
                                    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("button-1005-btnEl")));
                                    driver.findElement(By.id("button-1005-btnEl")).click();
                                    erro.printStackTrace();
                                }

                            } else {
                                resultado.setEtiqueta("ERRO EM TRIAGEM: INSTABILIDADE NO SAPIENS");
                            }
                            contador++;


                                if (contador == diferenca) {
                                    break;
                                }

                        }


                    }

            }



        }
        resultado.setDriver(driver);
        return resultado;
    }
}
