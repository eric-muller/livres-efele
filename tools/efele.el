(global-set-key [f3]          'pdf-show-page)
(global-set-key [(shift f3)]  'pdf-reset)

(global-set-key [f4]          'pagehere-show)
(global-set-key [(shift f4)]  'columnhere-show)

(global-set-key [f6]          'noteref)
(global-set-key [(shift f6)]  'notes)

(global-set-key [f7]          'reglue-pagehere-show)
(global-set-key [f8]          'reglue-paragraphs)

(global-set-key [f9]          'next-suspicious-pattern)

;; Other top-level functions which are not bound to keys:

;; clean-abby

;; correct

;; emptyline
;; fewlines
;; dots
;; threestars
;; onestar
;; hrule
;; hfrule
;; speaker

;;;;--------------------------- simultaneous bml/pdf viewing ---

;; two options, the last one is the active one

;; Option 1
;; on windows/old emacs: send command to Acrobat Reader via dde
;;
;; Adobe regularly changes the key to access AR. It is
;; of the form AcroViewR<number>
;;
;; To figure out what version you have, in regedit,
;; search for value AcroViewR
;;
;; HKEY_CLASSES_ROOT\acrobat\shell\open\ddeexec\application

(defun acrobat-command (s)
  (save-excursion
    (set-buffer (get-buffer-create " *ddeclient*"))
    (erase-buffer)
    (insert s)
    (call-process-region (point-min) (point-max)
                         "ddeclient" t t nil "AcroViewR22" "control")
    (if (= 0 (string-to-number (buffer-string))) t nil)))

(defun pdf-show-file (pdf)
  (acrobat-command (format "[DocOpen(\"%s\")]" pdf)))

(defun pdf-goto-page (pdf folio)
  (acrobat-command (format "[DocGoTo(\"%s\", %d)]" pdf folio)))

;; Option 2
;; on mac/modern emacs: use doc-viewer

(setq doc-view-resolution 500)

(defun pdf-show-file (pdf)
  (if (null pdf-frame)
      (setq pdf-frame (make-frame)))
  (message "showing file %s" pdf)
  (let ((f (selected-frame)))
    (select-frame pdf-frame)
    (find-file pdf)
    (select-frame f)))

(defun pdf-goto-page (pdf folio)
  (if (null pdf-frame)
      (pdf-show-file pdf))
  (let ((f (selected-frame)))
    (select-frame pdf-frame)
    (message "going to page %d" folio)
    (doc-view-goto-page folio)
    (select-frame f)))

;; the rest of this section is common to the two options

(defun pdf-reset ()
  (interactive)
  (setq pdf-file nil)
  (setq pdf-pages nil)
  (setq pdf-frame nil))

(pdf-reset)

;; to parse roman numbers
;; https://github.com/twlz0ne/emacs-pjb/blob/master/pjb-roman.el

(load "pjb-roman.el")

;; page spec: décrit la correspondance entre les numéros
;; de page imprimés et la position dans le PDF.
;;
;; C'est une liste de
;;   - [A-Z]<n>  pour n pages non numérotées.
;;   - <n>-<m>   pour des pages numérotéees de n à m
;;               en chiffres romains ou arabes
;; séparés par des espaces

(defun parse-pages-spec (s)
  (let (flat)
    (reverse
     (dolist (x (split-string s) flat)

       (cond
        ((string-match "^\\\([A-Za-z]+\\\)\\\([0-9]+\\\)$" x)
         (let ((n (string-to-number (match-string 2 x))))
           (dotimes (i n)
             (setq flat (cons (format "%s%d" (match-string 1 x) (1+ i)) flat)))))

        ((string-match "^\\\([ivxlc]+\\\)\\\(?:-\\\([ivxlc]+\\\)\\\)?$" x)
         (let ((n (from-roman (upcase (match-string 1 x))))
               (p (from-roman (upcase (match-string 2 x)))))
           (while (<= n p)
             (setq flat (cons (downcase (to-roman n)) flat))
             (setq n (1+ n)))))


        ((string-match "^\\\([0-9]+\\\)\\\(?:-\\\([0-9]+\\\)\\\)?$" x)
         (let ((n (string-to-number (match-string 1 x)))
               (p (string-to-number (match-string 2 x))))
           (while (<= n p)
             (setq flat (cons (format "%s" n) flat))
             (setq n (1+ n))))))))))


(defun find-facsimile (id)
  (save-excursion
    (goto-char (point-min))
    (if id
        (search-forward (concat "volume id=\"" id))
      (search-forward "volume"))

    (search-forward "facsimile")
    (re-search-forward "href=\"\\\(?:[^/\"]*/\\\)*\\\([^\"]*\\\)\"")
    (let ((zz (match-string-no-properties 1)))
      (if (and (length> zz 8)
               (string= (substring zz 0 9) "books?id="))
          (setq zz (substring zz 9 nil)))
      (if (string= (substring zz -4 nil)
                   ".pdf")
          (setq zz (substring zz 0 -4)))
      (setq newpdffile (concat (file-name-directory (buffer-file-name))
                               "scans/" zz ".pdf"))

      (if (not (equal pdf-file newpdffile))
          (progn
            (re-search-forward "pages=\"\\\([^\"]*\\\)\"")
            (setq pdf-pages (parse-pages-spec (match-string-no-properties 1)))
            (setq pdf-file newpdffile)
            (pdf-show-file pdf-file))))))

(defun pdf-show-page ()
  (interactive)
  (save-excursion
    (search-backward "pagenum ")
    (forward-char 8)

    (if (looking-at "v=")
        (progn
          (re-search-forward "\[\"']\\\([^\"']*\\\)[\"']")
          (find-facsimile (match-string-no-properties 1)))
      (find-facsimile nil))

    (re-search-forward "num=[\"']\\\([^\"']*\\\)[\"']")

    (let ((pagenum (match-string-no-properties 1))
          (folio 1)
          (pages  pdf-pages))
      (while (and pages
                  (not (equal pagenum (car pages))))
        (setq pages (cdr pages))
        (setq folio (1+ folio)))

      (if (null pages)
          (setq folio (+ folio (1- (string-to-number pagenum)))))

      (message "going to page %d" folio)

      (pdf-goto-page pdf-file folio))))


;;-------------------------------------------------------------
;; correct common problems in HTML generated by Abby FineReader
;; and get a bit closer to BML (e.g. <s> for small caps)
;;
;; note that "problems" is relative: I rarely do books with
;; bold, so bold is better removed entirely.

(defun clean-abby ()
  (interactive)
  (replace-regexp "<span style=\"font-weight *: *bold *;font-style *: *italic *;\">\\([^<]*\\)</span>"
                  "<i>\\1</i>" nil (point-min) (point-max))
  (replace-regexp "<span style=\"font-weight *: *bold;\">\\([^<]*\\)</span>"
                  "\\1" nil (point-min) (point-max))
  (replace-string "style=\"font-weight *: *bold;\""
                  ""  nil (point-min) (point-max))
  (replace-regexp "<span style=\"font-style:italic;\">\\(.\\)</span>"
                  "<i>\\1</i>" nil (point-min) (point-max))
  (replace-regexp "<span style=\"font-style *: *italic;\">\\([^<]*\\)</span>"
                  "<i>\\1</i>"  nil (point-min) (point-max))
  (replace-regexp "<span style=\"text-decoration:underline;\">\\([^<]*\\)</span>"
                  "\\1"  nil (point-min) (point-max))

  (replace-regexp "<span style=\"font-variant *: *small-caps.*;\">\\([^<]*\\)</span>"
                  "<s>\\1</s>" nil (point-min) (point-max))

  (replace-regexp "</i><i>" "" nil (point-min) (point-max))

  (replace-regexp "[ ]*<p>[
]*"
                  "
<p>"  nil (point-min) (point-max))

  (replace-regexp "[
]*</p>"
                  "</p>"  nil (point-min) (point-max))


  (replace-regexp "</i>\\(.\\)<i>"
                  "\\1" nil (point-min) (point-max))

  (replace-regexp "\\([!?;:»—]\\)" " \\1" nil (point-min) (point-max))
  (replace-regexp "\\([«—]\\)" "\\1 " nil (point-min) (point-max))

  (replace-regexp "  +"
                  " "  nil (point-min) (point-max))

  (replace-regexp "<p> " "<p>" nil (point-min) (point-max))

  (replace-regexp "'"
                  "’"  nil (point-min) (point-max))
  )

;;;------------------------------------------------------------
;;; find suspicious character patterns, e.g. a comma followed
;;; by something other than a space.

(defun build-re (x y)
  (if x
      (concat y "\\\(" (car x) "\\\)" (build-re (cdr x) "\\|"))
    ""))

(setq suspicious-pattern
      (build-re '("\\\.[^. <]"
                  "</sup>[^ ]"
                  "\\\. [^A-ZÇÉÊÈ»—]"
                  ",[^ ]"
                  ", [A-ZÇÉÊÈ]")
                ""))

(defun next-suspicious-pattern ()
  (interactive)
  (let ((before case-fold-search))
    (setq case-fold-search nil)
    (re-search-forward suspicious-pattern)
    (setq case-fold-search before))
  (showpageinacrobat)
  )

;;-------------------------------------------------------------
;; BML editing

(defun emptyline ()
  (interactive)
  (insert "<vsep class=\"emptyline\"/>\n"))

(defun fewlines ()
  (interactive)
  (insert "<vsep class=\"fewlines\"/>\n"))

(defun dots ()
  (interactive)
  (insert "<vsep class=\"dots\"/>\n"))

(defun threestars ()
  (interactive)
  (insert "<vsep class=\"threestars\"/>\n"))

(defun onestar ()
  (interactive)
  (insert "<vsep class=\"onestar\"/>\n"))

(defun hrule ()
  (interactive)
  (insert "<vsep class=\"rule\"/>\n"))

(defun hfrule ()
  (interactive)
  (insert "<vsep class=\"fullwidth-rule\"/>\n"))

;; decorate selected text with <speaker>
;;
(defun speaker ()
  (interactive)
  (if (< (point) (mark))
      (exchange-point-and-mark))

  (upcase-region (mark) (point))
  (insert "</speaker>")
  (exchange-point-and-mark)
  (insert "<speaker>"))


;; decorate selected text with <correct>
;;
(defun correct ()
  (interactive)
  (if (< (point) (mark))
      (exchange-point-and-mark))

  (let ((o (buffer-substring (mark) (point))))
    (exchange-point-and-mark)
    (insert "<correction original=\"")
    (exchange-point-and-mark)
    (insert "\">")
    (insert o)
    (setq o (point))
    (insert "</correction>")
    (goto-char o)))


;; insert a <pagenum> at the point
;; if between words, the point should be after the space
;; the page number is one more than the previous page number
;;
(defun pagehere ()
  (interactive)
  (setq c (point))
  (re-search-backward "num=\"\\\([^\"]*\\\)\"")
  (setq zz (match-string-no-properties 1))
  (cond ((string-match "^[0-9]*$" zz)
         (setq y "0")
         (setq z (int-to-string (1+ (string-to-number zz)))))
        ((string-match "^[ivxlc]*$" zz)
         (setq y "x")
         (setq z (downcase (to-roman (1+ (from-roman (upcase zz)))))))
        (t
         (setq y "X")
         (setq z (to-roman (1+ (from-roman zz))))))

  (goto-char c)

  (cond ((equal (buffer-substring (1- (point)) (point)) " ")
         (insert (format "<pagenum num=\"%s\"/>" z)))
        ((equal (buffer-substring (1- (point)) (point)) "
")
         (insert (format "<pagenum num=\"%s\"/>

" z)))
        (t
         (let ((c (point)))
           (search-backward " ")
           (forward-char 1)
           (setq d (point))
           (setq b (buffer-substring (point) c))
           (goto-char c)
           (search-forward " ")
           (backward-char 1)
           (setq a (buffer-substring c (point)))
           (insert "</pagenum>")
           (goto-char d)
           (insert (format "<pagenum num=\"%s\" b=\"%s-\" a=\"%s\">" z b a)))))
  (setq col 2))


;; insert a <colnum> at the point

(setq col 2)

(defun columnhere ()
  (interactive)
  (if (equal (buffer-substring (1- (point)) (point)) " ")
      (insert (format "<colnum num=\"%d\"/>" col))
    (let ((c (point)))
      (search-backward " ")
      (forward-char 1)
      (setq d (point))
      (setq b (buffer-substring (point) c))
      (goto-char c)
      (search-forward " ")
      (backward-char 1)
      (setq a (buffer-substring c (point)))
      (insert "</colnum>")
      (goto-char d)
      (insert (format "<colnum num=\"%d\" b=\"%s-\" a=\"%s\">" col b a))))
  (setq col (1+ col)))


(defun pagehere-show ()
  (interactive)
  (pagehere)
  (pdf-show-page))

(defun columnhere-show ()
  (interactive)
  (columnhere)
  (pdf-show-page))


;; with the point between two <p>, turn them in a single <p>
;;
(defun reglue-paragraphs ()
  (interactive)
  (search-backward "</p")
  (insert " ")
  (let ((c (point)))
    (search-forward "<p>")
    (delete-region c (point))))


;; with the point between two <p>, turn them in a single <p>
;; and insert a <pagenum>
;;
(defun reglue-pagehere-show ()
  (interactive)
  (end-of-line)
  (reglue-paragraphs)
  (pagehere)
  (pdf-show-page))


;; insert a <noteref> at the point.
;;
(defun noteref ()
  (interactive)
  (insert "<noteref noteid=\"note.")
  (setq x (point))
  (re-search-backward "num=\"\\\([^\"]*\\\)\"")
  (setq z (match-string-no-properties 1))
  (goto-char x)
  (insert z)
  (insert ".1\"/>")
  )

;; insert <notes> at the point
;;
(defun notes ()
  (interactive)
  (insert "<notes>\n")
  (insert "  <note id=")
  (setq x (point))
  (re-search-backward "noteid=\\\(\"[^\"]*\"\\\)")
  (setq z (match-string-no-properties 1))
  (goto-char x)
  (insert z)
  (insert ">\n")
  (insert "  </note>\n")
  (insert "</notes>")
  )
